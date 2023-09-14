package app.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import app.exceptions.NotEqualTypesException;
import app.exceptions.NotFoundAttributeExceptions;

public class DB<T> {
    private final String databaseName;
    private final String pipe;
    private Map<String, Object> attributesGenerics = new HashMap<>();

    public DB(String databaseName, String pipe) {
        this.databaseName = databaseName + ".txt";
        this.pipe = pipe;
        createDatabase();
    }

    private void createDatabase() {
        File db = new File(databaseName);
        if (!db.exists()) {
            try {
                db.createNewFile();
            } catch (IOException ioex) {
                System.out.println(ioex.getMessage());
            }
        }
    }

    public void addData(T data) throws IllegalAccessException {
        ArrayList<Object> values = getValuesFromObject(data);
        String formattedData = formatDataForSaving(values);
        try (FileWriter fw = new FileWriter(databaseName, true)) {
            fw.append(formattedData);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }

    private String formatDataForSaving(ArrayList<Object> values) {
        StringBuilder formattedData = new StringBuilder();
        for (int i = 0; i < values.size(); ++i) {
            if (i < values.size() - 1)
                formattedData.append(values.get(i) + pipe);
            else
                formattedData.append(values.get(i) + "\n");
        }
        return formattedData.toString();
    }

    public T get(int index, Class<T> typeClass) throws IOException, ReflectiveOperationException {
        return getData(typeClass).get(index);
    }

    public void pruebita(String nombre) {
        System.out.println(nombre);
    }

    public ArrayList<T> getData(Class<T> typeClass) throws IOException, ReflectiveOperationException {
        ArrayList<T> data = new ArrayList<>();
        Field[] fields;
        T instance;
        Constructor<T> constructor;
        ArrayList<String> values;
        StringTokenizer tokenizer;

        try (BufferedReader bf = new BufferedReader(new FileReader(databaseName))) {
            constructor = typeClass.getConstructor();
            values = getDataFromDatabase();
            for (String value : values) {
                instance = constructor.newInstance();
                fields = instance.getClass().getDeclaredFields();
                tokenizer = new StringTokenizer(value, pipe);
                for (Field field : fields) {
                    if (tokenizer.hasMoreTokens()) {
                        String tokenValue = tokenizer.nextToken();
                        setValueInstance(instance, field, tokenValue);
                    }
                }
                data.add(instance);
            }
        }
        return data;
    }

    private void setValueInstance(Object instance, Field field, Object value) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType == int.class || fieldType == Integer.class) {
            field.set(instance, Integer.parseInt("" + value));
        } else if (fieldType == String.class) {
            field.set(instance, value);
        }
    }

    public void remove(int position) throws IOException {
        ArrayList<String> values = getDataFromDatabase();
        String newData = new String("");
        int it = 0;
        for (String value : values) {
            if (it != position)
                newData += value + "\n";
            ++it;
        }
        FileWriter fw = new FileWriter(databaseName, false);
        fw.write(newData);
        fw.close();
    }

    private void getAttributesFromGeneric(Class<T> typeClass) {
        attributesGenerics.clear();
        Field[] fields = typeClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            attributesGenerics.put(field.getName(), field.getType());
        }
    }

    private void checkAttributeValue(String attribute, Object value)
            throws NotFoundAttributeExceptions, NotEqualTypesException {
        if (attributesGenerics.get(attribute) == null) {
            throw new NotFoundAttributeExceptions();
        }
        if (!(value.getClass().getName().equals("java.lang.Integer")
                && attributesGenerics.get(attribute).toString().equals("int"))) {
            if (attributesGenerics.get(attribute) != value.getClass()) {
                throw new NotEqualTypesException();
            }

        }
    }

    Constructor<T> getConstructorGeneric(Class<T> typeClass) throws NoSuchMethodException {
        return typeClass.getDeclaredConstructor();
    }

    public ArrayList<T> find(String attribute, Object value, Class<T> typeClass)
            throws IOException, ReflectiveOperationException, NotEqualTypesException, NotFoundAttributeExceptions {

        getAttributesFromGeneric(typeClass);
        checkAttributeValue(attribute, value);

        ArrayList<T> data = new ArrayList<>();
        ArrayList<String> values = getDataFromDatabase();
        Constructor<T> constructor = getConstructorGeneric(typeClass);

        for (String databaseValue : values) {
            StringTokenizer tokenizer = new StringTokenizer(databaseValue, pipe);
            T instance = constructor.newInstance();
            Field[] fields = instance.getClass().getDeclaredFields();
            Map<Integer, String> tempTokens = new HashMap<>();

            for (int i = 0; tokenizer.hasMoreTokens(); ++i) {
                String tokenValue = tokenizer.nextToken();
                tempTokens.put(i, tokenValue);
                if (tokenValue.equals(value.toString())) {
                    setValueInstance(instance, fields[i], tokenValue);
                    while (tokenizer.hasMoreTokens()) {
                        tokenValue = tokenizer.nextToken();
                        setValueInstance(instance, fields[++i], tokenValue);
                    }
                    for (Integer positionToken : tempTokens.keySet()) {
                        setValueInstance(instance, fields[positionToken], tempTokens.get(positionToken));
                    }
                    data.add(instance);
                }
            }
        }
        return data;
    }

    public void clear() throws IOException {
        try (FileWriter fw = new FileWriter(databaseName, false)) {
            fw.write("");
        }
    }

    private ArrayList<String> getDataFromDatabase() throws IOException {
        ArrayList<String> values = new ArrayList<>();
        try (BufferedReader bf = new BufferedReader(new FileReader(databaseName))) {
            String line = "";
            while ((line = bf.readLine()) != null)
                values.add(line);
        }
        return values;
    }

    private ArrayList<Object> getValuesFromObject(T data) throws IllegalAccessException {
        Class<?> type = data.getClass();
        Field[] fields = type.getDeclaredFields();
        ArrayList<Object> values = new ArrayList<>();
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = field.get(data);
            values.add(value);
        }
        return values;
    }
}

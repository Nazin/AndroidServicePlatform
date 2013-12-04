package pl.edu.agh.mobile.serviceplatform.services;

import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractFactory {

    public abstract void process(String inputFile, String outputFile) throws IOException;

    public static AbstractFactory create(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        Class<?> temp = Class.forName(className);
        return (AbstractFactory) temp.cast(temp.newInstance());
    }
}

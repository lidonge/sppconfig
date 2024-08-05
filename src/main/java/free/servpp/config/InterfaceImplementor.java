package free.servpp.config;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;

/**
 * @author lidong@date 2024-08-05@version 1.0
 */

public class InterfaceImplementor {
    static final String INTERFACENAME = "com.typesafe.config.Config";
    static final String INITIALIMPLNAME = "AAA";
    static final String NEWIMPLNAME = "ConfigImpl";

    public static void main(String[] args) {
//        if (args.length < 3) {
//            System.out.println("Usage: java InterfaceImplementor <interface-name> <initial-impl-name> <new-impl-name>");
//            return;
//        }

//        String interfaceName = args[0];
//        String initialImplName = args[1];
//        String newImplName = args[2];
        String interfaceName = INTERFACENAME;
        String initialImplName = INITIALIMPLNAME;
        String newImplName = NEWIMPLNAME;

        try {
            generateImplementation(interfaceName, initialImplName, newImplName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateImplementation(String interfaceName, String initialImplName, String newImplName) throws ClassNotFoundException, IOException {
        Class<?> interfaceClass = Class.forName(interfaceName);
//        Class<?> initialImplClass = Class.forName(initialImplName);

        StringBuilder code = new StringBuilder();
        code.append("public class ").append(newImplName).append(" implements ").append(interfaceName).append(" {\n");
        code.append("    private final ").append(interfaceName).append(" delegate = new ").append(initialImplName).append("();\n\n");

        for (Method method : interfaceClass.getDeclaredMethods()) {
            appendMethodWithGenerics(code, method);
        }
        code.append("}\n");

//        String fileName = newImplName + ".java";
//        try (FileWriter writer = new FileWriter(fileName)) {
//            writer.write(code.toString());
//        }
//
//        System.out.println("Generated " + fileName);
        System.out.println(code);

    }

    private static void appendMethodWithGenerics(StringBuilder code, Method method) {
        code.append("    @Override\n");

        // Append method return type with generics
        Type returnType = method.getGenericReturnType();
        code.append("    public ").append(getTypeString(returnType)).append(" ").append(method.getName()).append("(");

        // Append method parameters with generics
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                code.append(", ");
            }
            code.append(getTypeString(parameters[i].getParameterizedType())).append(" ").append(parameters[i].getName());
        }
        code.append(") {\n");

        // Append method body with delegation
        if (!returnType.equals(void.class)) {
            code.append("        return ");
        } else {
            code.append("        ");
        }
        code.append("delegate.").append(method.getName()).append("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                code.append(", ");
            }
            code.append(parameters[i].getName());
        }
        code.append(");\n");
        code.append("    }\n\n");
    }

    private static String getTypeString(Type type) {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder typeString = new StringBuilder(getTypeString(pType.getRawType()));
            Type[] typeArguments = pType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                typeString.append("<");
                for (int i = 0; i < typeArguments.length; i++) {
                    if (i > 0) {
                        typeString.append(", ");
                    }
                    typeString.append(getTypeString(typeArguments[i]));
                }
                typeString.append(">");
            }
            return typeString.toString();
        } else if (type instanceof TypeVariable<?>) {
            return ((TypeVariable<?>) type).getName();
        } else {
            return type.toString();
        }
    }

}
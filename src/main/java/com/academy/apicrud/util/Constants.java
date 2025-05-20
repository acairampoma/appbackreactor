package com.academy.apicrud.util;

public class Constants {
    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_NO_CONTENT = 204;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;

    // CRUD operations
    public static final String GET = "Operación GET realizada con éxito";
    public static final String POST = "Operación POST realizada con éxito";
    public static final String PUT = "Operación PUT realizada con éxito";
    public static final String DELETE = "Operación DELETE realizada con éxito";

    // Estado de usuarios
    public static final String USUARIO_ACTIVADO = "ACTIVO";
    public static final String USUARIOS_ACTIVADOS = "Usuarios activados con éxito";
    public static final String USUARIOS_INACTIVADOS = "Usuarios inactivados con éxito";

    // Mensajes para médicos
    public static final String MEDICO_NOT_FOUND = "Médico no encontrado";
    public static final String ESPECIALIDAD_NOT_FOUND = "Especialidad no encontrada";
}
package com.cloud.publishing.common.constants.employee;

public class EmployeeMessage {
    private EmployeeMessage() {
    }

    public static final String FAILED_TO_ADD_MSG = "Не удалось добавить сотрудника";
    public static final String FAILED_TO_UPDATE_WITH_ID_MSG = "Не удалось обновить сотрудника с EDUCATION_ID %d";
    public static final String FAILED_TO_GET_MSG = "Не удалось получить данные сотрудника";
    public static final String FAILED_TO_LIST_MSG = "Не удалось получить список сотрудников";
    public static final String FAILED_TO_DELETE_MSG = "Не удалось удалить сотрудника";
    public static final String FAILED_TO_CHECK_MESSAGE = "Не удалось проверить существование сотрудника";
    public static final String EMPLOYEE_NOT_FOUND_MSG = "Сотрудник не найден";
    public static final String FAILED_TO_RESET_CHIEF_EDITOR = "Не удалось сбросить статус главного редактора";
    public static final String FAILED_TO_ADD_NO_ID = "Создание сотрудника не удалось, EDUCATION_ID не получен";
    public static final String FAILED_TO_ADD_NO_ROWS = "Создание сотрудника не удалось, ни одна запись не была добавлена";
    public static final String PASSWORD_MISMATCH_OR_CONFIRMATION_MISSING_MSG = "Пароли не совпадают или подтверждение отсутствует";
}
package org.example.practice_tasks_1;

import org.apache.commons.lang3.StringUtils;

public class MaskPhone {

//    6. Метод maskPhone(String phone) - скрыть середину номера:
//    видны первые 5 символов и последние 4, между ними три звездочки.
//    Если строка короче 9 символов - вернуть как есть.
    private static final int BORDER_LENGTH = 9;

    public static String maskPhone(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new IllegalArgumentException("phoneNumber can not be empty");
        }

        if (phoneNumber.length() <= BORDER_LENGTH) {
            return phoneNumber;
        }

        int phoneNumberLength = phoneNumber.length();

        return phoneNumber.substring(0, 5) +
                "*".repeat(phoneNumberLength - 9) +
                phoneNumber.substring(phoneNumberLength - 4, phoneNumberLength);
    }
}
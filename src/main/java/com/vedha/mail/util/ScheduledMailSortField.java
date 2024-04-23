package com.vedha.mail.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ScheduledMailSortField {

    ID("mailId"),

    SCHEDULED_DATE("scheduledTime");

    private final String field;
}

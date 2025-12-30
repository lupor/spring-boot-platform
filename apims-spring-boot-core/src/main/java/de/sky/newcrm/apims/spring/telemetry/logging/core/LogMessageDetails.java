/*
 * Copyright (C) 2023-2024 Sky Deutschland Fernsehen GmbH & Co. KG. All rights reserved.
 * This file and its contents are the sole property of Sky Deutschland Fernsehen GmbH & Co. KG.
 */
package de.sky.newcrm.apims.spring.telemetry.logging.core;

import lombok.*;
import org.springframework.util.StringUtils;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LogMessageDetails {
    private String resultIdentifier;
    private String durationSecondsValue;
    private String additionalArgs;
    private String args;

    public void appendDetailsMessage(boolean newestImplementation, boolean beforeCase, StringBuilder msg) {
        if (newestImplementation) {
            if (beforeCase) {
                msg.append("| INVOKE | ");
            } else {
                msg.append("| RETURN | ")
                        .append(resultIdentifier)
                        .append(" | ")
                        .append(durationSecondsValue)
                        .append(" | ");
            }
            if (StringUtils.hasLength(additionalArgs)) {
                msg.append(additionalArgs).append(" | ");
            }
        } else {
            if (beforeCase) {
                if (StringUtils.hasLength(additionalArgs)) {
                    msg.append(additionalArgs).append(" ");
                }
                msg.append("args=");
            } else {
                msg.append(durationSecondsValue)
                        .append(" ")
                        .append(resultIdentifier)
                        .append(" : ");
            }
        }
        msg.append(args);
    }
}

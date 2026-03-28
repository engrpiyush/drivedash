package com.drivemond.chatting.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotNull(message = "Channel ID is required")
    private UUID channelId;

    private String message;
}

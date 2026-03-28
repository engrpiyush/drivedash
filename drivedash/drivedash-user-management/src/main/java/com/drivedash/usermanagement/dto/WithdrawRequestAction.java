package com.drivedash.usermanagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WithdrawRequestAction {

    private boolean approved;

    private String rejectionCause;
}

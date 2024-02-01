package pas.gV.restapi.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordDTORequest {
    private String actualPassword;
    private String newPassword;
    private String confirmationPassword;
}

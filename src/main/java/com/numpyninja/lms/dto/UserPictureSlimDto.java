package com.numpyninja.lms.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPictureSlimDto {

    private Long userFileId;

    private String userFileType;

    private String userFilePath;

}

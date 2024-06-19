package com.coherentsolutions.pot.insurance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClaimDTO {

  private UUID id;

  @NotBlank(message = "Claim number is required")
  private String claimNumber;

  @NotBlank(message = "Consumer is required")
  private String consumer;

  @NotBlank(message = "Employer is required")
  private String employer;

  @NotNull(message = "Date of service is required")
  private Date dateOfService;

  @NotBlank(message = "Plan is required")
  private String plan;

  @NotNull(message = "Amount is required")
  @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
  private BigDecimal amount;

  @NotBlank(message = "Status is required")
  private String status;
}

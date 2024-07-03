package com.coherentsolutions.pot.insurance.packages;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coherentsolutions.pot.insurance.constants.PackagePayrollFrequency;
import com.coherentsolutions.pot.insurance.constants.PackageStatus;
import com.coherentsolutions.pot.insurance.constants.PackageType;
import com.coherentsolutions.pot.insurance.controller.PackageController;
import com.coherentsolutions.pot.insurance.dto.PackageDTO;
import com.coherentsolutions.pot.insurance.service.PackageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PackageController.class)
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
public class PackageIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PackageService packageService;

  @Test
  void testGetFilteredSortedPackages() throws Exception {
    List<PackageDTO> packages = List.of(
        createBasicPackageDTO().name("Package A").build(),
        createBasicPackageDTO().name("Package B").build(),
        createBasicPackageDTO().name("Package C").build()
    );
    Page<PackageDTO> pagedPackages = new PageImpl<>(packages, PageRequest.of(0, 3), packages.size());

    Mockito.when(packageService.getFilteredSortedPackages(Mockito.any(), Mockito.any()))
        .thenReturn(pagedPackages);

    mockMvc.perform(get("/v1/packages/filtered")
            .param("name", "Package A")
            .param("status", "ACTIVE")
            .param("page", "0")
            .param("size", "3")
            .param("sort", "name,asc")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(3))
        .andExpect(jsonPath("$.content[0].name").value("Package A"))
        .andExpect(jsonPath("$.content[1].name").value("Package B"))
        .andExpect(jsonPath("$.content[2].name").value("Package C"));
  }


  @Test
  void testAddPackage() throws Exception {
    PackageDTO packageDTO = createBasicPackageDTO().build();
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    Mockito.when(packageService.addPackage(Mockito.any())).thenReturn(packageDTO);

    mockMvc.perform(post("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.name").value("Basic Health Package"));
  }

  @Test
  void testGetAllPackages() throws Exception {
    mockMvc.perform(get("/v1/packages"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andReturn();
  }

  @Test
  void testGetPackageById() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = createBasicPackageDTO()
        .id(packageId)
        .build();

    Mockito.when(packageService.getPackageById(packageId)).thenReturn(packageDTO);

    mockMvc.perform(get("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(packageId.toString()));
  }

  @Test
  void testUpdatePackage() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = createBasicPackageDTO()
        .id(packageId)
        .name("Updated Health Package")
        .build();
    String packageJson = objectMapper.writeValueAsString(packageDTO);

    Mockito.when(packageService.updatePackage(Mockito.any())).thenReturn(packageDTO);

    mockMvc.perform(put("/v1/packages")
            .contentType(MediaType.APPLICATION_JSON)
            .content(packageJson))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Updated Health Package"))
        .andReturn();
  }

  @Test
  void testDeactivatePackage() throws Exception {
    UUID packageId = UUID.fromString("83d8456f-95bb-4f84-859f-8da1f6abac1a");
    PackageDTO packageDTO = createBasicPackageDTO()
        .id(packageId)
        .status(PackageStatus.DEACTIVATED)
        .build();

    Mockito.when(packageService.deactivatePackage(packageId)).thenReturn(packageDTO);

    mockMvc.perform(delete("/v1/packages/{id}", packageId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("DEACTIVATED"));
  }

  private PackageDTO.PackageDTOBuilder createBasicPackageDTO() {
    return PackageDTO.builder()
        .id(UUID.randomUUID())
        .name("Basic Health Package")
        .status(PackageStatus.ACTIVE)
        .payrollFrequency(PackagePayrollFrequency.MONTHLY)
        .startDate(LocalDate.of(2024, 1, 1))
        .endDate(LocalDate.of(2025, 1, 1))
        .type(PackageType.STANDARD)
        .contributions(150.00);
  }
}
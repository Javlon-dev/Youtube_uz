package com.company.controller;

import com.company.dto.ReportDTO;
import com.company.enums.ProfileRole;
import com.company.service.ReportService;
import com.company.util.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Api(tags = "Report")
public class ReportController {

    private final ReportService reportService;

    /**
     * PUBLIC
     */

    @ApiOperation(value = "Create", notes = "Method used for write report for video or channel",
            authorizations = @Authorization(value = "JWT Token"))
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid ReportDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        return ResponseEntity.ok(reportService.create(dto, JwtUtil.getIdFromHeader(request)));
    }

    /**
     * ADMIN
     */

    @ApiOperation(value = "List", notes = "Method used for get pagination report list",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list")
    public ResponseEntity<?> reportListPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(reportService.reportListPagination(page, size));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete report by id",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/adm/{reportId}")
    public ResponseEntity<?> reportListPagination(@PathVariable("reportId") String reportId,
                                                  HttpServletRequest request) {
        log.info("DELETE {}", reportId);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(reportService.delete(reportId));
    }

    @ApiOperation(value = "List", notes = "Method used for get pagination report list by profileId",
            authorizations = @Authorization(value = "JWT Token"))
    @GetMapping("/adm/list/{profileId}")
    public ResponseEntity<?> reportListPagination(@RequestParam(value = "page", defaultValue = "0") int page,
                                                  @RequestParam(value = "size", defaultValue = "5") int size,
                                                  @PathVariable("profileId") String profileId,
                                                  HttpServletRequest request) {
        log.info("LIST page={} size={}", page, size);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(reportService.reportListPaginationByProfileId(page, size, profileId));
    }

}

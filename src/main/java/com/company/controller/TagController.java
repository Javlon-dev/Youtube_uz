package com.company.controller;

import com.company.dto.TagDTO;
import com.company.enums.ProfileRole;
import com.company.service.TagService;
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
@RequestMapping("/tag")
@RequiredArgsConstructor
@Api(tags = "Tag")
public class TagController {

    private final TagService tagService;


    /**
     * PUBLIC
     */

    @ApiOperation(value = "Create", notes = "Method used for create tag")
    @PostMapping("/public")
    public ResponseEntity<?> create(@RequestBody @Valid TagDTO dto,
                                    HttpServletRequest request) {
        log.info("CREATE {}", dto);
        JwtUtil.getIdFromHeader(request);
        return ResponseEntity.ok(tagService.create(dto));
    }

    @ApiOperation(value = "List", notes = "Method used for get list of tags")
    @GetMapping("")
    public ResponseEntity<?> list(@RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "5") int size) {
        log.info("LIST page={} size={}", page, size);
        return ResponseEntity.ok(tagService.list(page, size));
    }


    /**
     * ADMIN
     */

    @ApiOperation(value = "Update", notes = "Method used for update tag",
            authorizations = @Authorization(value = "JWT Token"))
    @PutMapping("/adm/{id}")
    public ResponseEntity<?> update(@PathVariable("id") String id,
                                    @RequestBody @Valid TagDTO dto,
                                    HttpServletRequest request) {
        log.info("UPDATE {}", dto);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(tagService.update(id, dto));
    }

    @ApiOperation(value = "Delete", notes = "Method used for delete tag",
            authorizations = @Authorization(value = "JWT Token"))
    @DeleteMapping("/adm/{id}/delete")
    public ResponseEntity<?> delete(@PathVariable("id") String id, HttpServletRequest request) {
        log.info("DELETE {}", id);
        JwtUtil.getIdFromHeader(request, ProfileRole.ADMIN);
        return ResponseEntity.ok(tagService.delete(id));
    }
}

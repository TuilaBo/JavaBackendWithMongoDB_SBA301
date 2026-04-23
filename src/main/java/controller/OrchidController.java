package controller;

import dto.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pojo.Orchid;
import service.OrchidService;

import java.util.List;

@RestController
@RequestMapping("/api/orchids")
@Tag(name = "Orchid Management", description = "APIs for managing orchids")
public class OrchidController {
    
    @Autowired
    private OrchidService orchidService;
    
    @Operation(summary = "Get all orchids", description = "Returns list of all orchids")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved orchids",
            content = @Content(schema = @Schema(implementation = Orchid.class)))
    @GetMapping
    public ResponseEntity<ApiResponse<List<Orchid>>> getAllOrchids() {
        List<Orchid> data = orchidService.getAllOrchids();
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchids retrieved successfully", data));
    }
    
    @Operation(summary = "Get orchid by ID", description = "Returns a specific orchid by ID")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orchid found",
                    content = @Content(schema = @Schema(implementation = Orchid.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Orchid not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Orchid>> getOrchidById(@PathVariable String id) {
        Orchid data = orchidService.getOrchidById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Orchid not found"));
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchid retrieved successfully", data));
    }
    
    @Operation(summary = "Create new orchid", description = "Creates a new orchid")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Orchid created successfully",
                    content = @Content(schema = @Schema(implementation = Orchid.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<Orchid>> createOrchid(@RequestBody Orchid orchid) {
        Orchid data = orchidService.createOrchid(orchid);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(HttpStatus.CREATED.value(), "Orchid created successfully", data));
    }
    
    @Operation(summary = "Update orchid", description = "Updates an existing orchid")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Orchid updated successfully",
                    content = @Content(schema = @Schema(implementation = Orchid.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Orchid not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Orchid>> updateOrchid(@PathVariable String id, @RequestBody Orchid orchid) {
        Orchid data = orchidService.updateOrchid(id, orchid);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchid updated successfully", data));
    }
    
    @Operation(summary = "Delete orchid", description = "Deletes an orchid")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Orchid deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Orchid not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> deleteOrchid(@PathVariable String id) {
        orchidService.deleteOrchid(id);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchid deleted successfully", null));
    }
    
    @Operation(summary = "Get orchids by category", description = "Returns orchids filtered by category")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved orchids by category")
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Orchid>>> getOrchidsByCategory(@PathVariable Long categoryId) {
        List<Orchid> data = orchidService.getOrchidsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchids retrieved successfully", data));
    }
    
    @Operation(summary = "Search orchids by name", description = "Returns orchids matching the search term")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Successfully retrieved matching orchids")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Orchid>>> searchOrchids(@RequestParam String name) {
        List<Orchid> data = orchidService.searchOrchidsByName(name);
        return ResponseEntity.ok(ApiResponse.of(HttpStatus.OK.value(), "Orchids retrieved successfully", data));
    }
}

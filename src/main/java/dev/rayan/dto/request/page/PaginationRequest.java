package dev.rayan.dto.request.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.panache.common.Page;
import jakarta.validation.constraints.Min;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public final class PaginationRequest {

    @QueryParam("pageNumber")
    @DefaultValue("1")
    @Min(message = "Page number can´t be less than 1!", value = 1)
    @Setter
    int pageNumber;

    @QueryParam("pageSize")
    @DefaultValue("10")
    @Min(message = "Page size can´t be less than 1!", value = 1)
    int pageSize;

    @JsonIgnore
    public Page getPage() {
        return Page.of(getPageIndex(), pageSize);
    }

    @JsonProperty("pageIndex")
    public int getPageIndex() {
        return pageNumber - 1;
    }

}
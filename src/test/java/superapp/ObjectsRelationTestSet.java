package superapp;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import superapp.data.UserRole;
import superapp.logic.boundaries.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ObjectsRelationTestSet extends BaseTestSet{
    @Test
    @DisplayName("Successful create object")
    public void SuccessfulCreateObject() {

        // GIVEN
        // 1. the server is up and running
        // 2. the database is up and running
        // 3. db contain user - initialize in setup


        // when
        // A POST request is made to the path "superapp/objects" with SuperAppObjectBoundary

        String type = "EVENT";
        String alias = "demo";
        Boolean active = true;
        Location location = new Location(10.200, 10.200);
        CreatedBy createdBy = new CreatedBy().setUserId(new UserID(this.springApplicationName,"demo@gmail.com" ));
        Map<String, Object> objectDetails = new HashMap<>();
        objectDetails.put("details", "String object demo");

        SuperAppObjectBoundary postObject = help_PostObjectBoundary(null, type, alias, null,
                active,  location, createdBy,  objectDetails);
        // then
        // the server response with status 2xx code and return  SuperAppObjectBoundary as json

        SuperAppObjectBoundary objectFromGet = this.help_GetObjectBoundary(
                                                postObject.getObjectId().getInternalObjectId(),
                                                postObject.getObjectId().getSuperapp());

        assertThat(objectFromGet)
                                .isNotNull()
                                .usingRecursiveComparison()
                                .isEqualTo(postObject);
    }

}

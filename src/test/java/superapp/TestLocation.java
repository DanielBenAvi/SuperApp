package superapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import superapp.logic.boundaries.*;

import java.util.Date;
import java.util.HashMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestLocation extends BaseTestSet{



    private final String admin = "ADMIN";
    private final String miniappRole = "MINIAPP_USER";
    private final String superappRole = "SUPERAPP_USER";

    private UserBoundary createUser(String email, String role) {

        String username = "demo";
        String avatar = "demo";
        return help_PostUserBoundary(email, role, username, avatar);
    }

    private SuperAppObjectBoundary createObject(String type, String email, Location location) {

        help_PutUserBoundary(new UserBoundary().setRole(superappRole), email);
        ObjectId objectId = new ObjectId();

        String alias = "demo name";
        boolean active = true;
        Date createdTimestamp = new Date();

        CreatedBy createdBy = new CreatedBy().setUserId(new UserId().setSuperapp(springApplicationName).setEmail(email));

        // post object
        return help_PostObjectBoundary(objectId, type, alias, createdTimestamp, active, location, createdBy, new HashMap<>());
    }

    @Test
    public void
    test(){

        String email = "demo@gmail.com";
        // create users
        UserBoundary userBoundary = createUser(email, superappRole);
        createObject("center cir", email, new Location(35.36320943407151, 32.927894974728034));
        createObject("out cir 1", email, new Location(35.351840632183325, 33.17157248247106));
        createObject("out cir 2", email, new Location(35.350216517627786, 32.69447947587571));
        createObject("out cir 3", email, new Location(35.62794010661821, 32.91562524141315));
        createObject("out cir 4", email, new Location(35.09523053241378, 32.87334980810748));
        createObject("in cir 3", email, new Location(35.51912443140023, 32.85425106522189));
        createObject("in cir 2", email, new Location(35.46552865106773, 33.04096894282121));
        createObject("in cir 1", email, new Location(35.156946885523524, 33.04233039653295));
        createObject("in cir 4", email, new Location(35.17481214563364, 32.7873730895149));
        createObject("in cir out sq 1", email, new Location(35.11328926399358, 32.92446430365327));
        createObject("in cir out sq 2", email, new Location(35.362026432420066, 33.12686207904888));
        createObject("out cir in sq 2", email, new Location(35.573939141950035, 33.09142149636196));
        createObject("out cir in sq 1", email, new Location(35.1784439476497, 33.10251851085363));
        createObject("out cir in sq 4", email, new Location(35.15005912509292, 32.72443561142859));
        createObject("out cir in sq 3", email, new Location( 35.55880056991981, 32.7323951201534));
        createObject("in cir out sq 3", email, new Location(35.617963577692024, 32.92369875323038));

        int x = 1;

    }
    /**
     *         "center cir":
     *           35.36320943407151, 32.927894974728034
     *
     *           "out cir 1"
     *           35.351840632183325, 33.17157248247106
     *
     *         "out cir 2"
     *           35.350216517627786, 32.69447947587571
     *
     *         "out cir 3"
     *           35.62794010661821, 32.91562524141315
     *
     *           "out cir 4":
     *           35.09523053241378, 32.87334980810748
     *
     *         "in cir 3"
     *           35.51912443140023, 32.85425106522189
     *
     *         "in cir 2":
     *           35.46552865106773, 33.04096894282121
     *
     *
     *
     *
     *
     *         "in cir 1"
     *           35.156946885523524, 33.04233039653295
     *
     *
     *
     *
     *         "in cir 4"
     *           35.17481214563364, 32.7873730895149
     *
     *         "in cir out sq 1"
     *           35.11328926399358, 32.92446430365327
     *
     *
     *
     *
     *         "in cir out sq 2"
     *           35.362026432420066, 33.12686207904888
     *
     *
     *
     *
     *
     *         "out cir in sq 2"
     *           35.573939141950035, 33.09142149636196
     *
     *
     *
     *
     *
     *
     *
     *         "out cir in sq 1"
     *           35.1784439476497, 33.10251851085363
     *
     *
     *
     *
     *         "out cir in sq 4"
     *           35.15005912509292, 32.72443561142859
     *
     *
     *
     *
     *
     *         "out cir in sq 3"
     *           35.55880056991981, 32.7323951201534
     *
     *
     *
     *         "in cir out sq 3"
     *           35.617963577692024, 32.92369875323038
     */
}

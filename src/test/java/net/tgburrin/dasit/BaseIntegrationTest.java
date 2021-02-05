package net.tgburrin.dasit;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
@ExtendWith(TestSetup.class)
//@Sql(scripts = "/schema.sql") // this does not work due to a bug in the extension
public class BaseIntegrationTest {}

package net.tgburrin.dasit;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;

//import net.tgburrin.dasit.Group.GroupStatus;

class ApplicationConfig extends AbstractJdbcConfiguration {
	@Bean
	NamedParameterJdbcOperations namedParameterJdbcOperations(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	TransactionManager transactionManager(DataSource dataSource) {
		return new DataSourceTransactionManager(dataSource);
	}

	/*
	@Bean
	QueryMappingConfiguration rowMappers() {
		return new DefaultQueryMappingConfiguration()
				.registerRowMapper(Dataset.class, new DatasetRowMapper());
	}
	 */
	/*
    @Override
    public JdbcCustomConversions jdbcCustomConversions() {

      return new JdbcCustomConversions(Collections.singletonList(GroupStatusToCharConverter.INSTANCE));

    }

    @WritingConverter
    enum GroupStatusToCharConverter implements Converter<GroupStatus, String> {
    	INSTANCE;

    	@Override
    	public String convert(GroupStatus src) {
    		return src.toString();
    	}
    }
	 */
}

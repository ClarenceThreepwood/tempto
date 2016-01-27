/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.teradata.tempto.internal.query;

import com.teradata.tempto.configuration.Configuration;
import com.teradata.tempto.configuration.KeyUtils;
import com.teradata.tempto.query.JdbcConnectivityParamsState;

import java.util.Optional;
import java.util.Set;

public class JdbcConnectionsConfiguration
{

    private static final String DATABASES_CONFIGURATION_SECTION = "databases";
    private static final String JDBC_DRIVER_CLASS = "jdbc_driver_class";
    private static final String JDBC_URL_KEY = "jdbc_url";
    private static final String JDBC_USER_KEY = "jdbc_user";
    private static final String JDBC_PASSWORD_KEY = "jdbc_password";
    private static final String JDBC_POOLING_KEY = "jdbc_pooling";
    private static final String JDBC_JAR = "jdbc_jar";
    private static final String ALIAS_KEY = "alias";
    private static final String PREPARE_STATEMENT_KEY = "prepare_statement";
    private static final String KERBEROS_PRINCIPAL_KEY = "kerberos_principal";
    private static final String KERBEROS_KEYTAB_KEY = "kerberos_keytab";

    private final Configuration configuration;

    public JdbcConnectionsConfiguration(Configuration configuration) {this.configuration = configuration;}

    public Set<String> getDefinedJdcbConnectionNames()
    {
        return configuration.getSubconfiguration(DATABASES_CONFIGURATION_SECTION).listKeyPrefixes(1);
    }

    public JdbcConnectivityParamsState getConnectionConfiguration(String connectionName)
    {

        Configuration connectionConfiguration = getDatabaseConnectionSubConfiguration(connectionName);
        Optional<String> alias = connectionConfiguration.getString(ALIAS_KEY);
        if (alias.isPresent()) {
            connectionConfiguration = getDatabaseConnectionSubConfiguration(alias.get());
        }

        return JdbcConnectivityParamsState.builder()
                .setName(connectionName)
                .setDriverClass(connectionConfiguration.getStringMandatory(JDBC_DRIVER_CLASS))
                .setUrl(connectionConfiguration.getStringMandatory(JDBC_URL_KEY))
                .setUser(connectionConfiguration.getStringMandatory(JDBC_USER_KEY))
                .setPassword(connectionConfiguration.getStringMandatory(JDBC_PASSWORD_KEY))
                .setPooling(connectionConfiguration.getBoolean(JDBC_POOLING_KEY).orElse(false))
                .setJar(connectionConfiguration.getString(JDBC_JAR))
                .setPrepareStatement(connectionConfiguration.getString(PREPARE_STATEMENT_KEY))
                .setKerberosPrincipal(connectionConfiguration.getString(KERBEROS_PRINCIPAL_KEY))
                .setKerberosKeytab(connectionConfiguration.getString(KERBEROS_KEYTAB_KEY))
                .build();
    }

    private Configuration getDatabaseConnectionSubConfiguration(String connectionName)
    {
        return configuration.getSubconfiguration(KeyUtils.joinKey(DATABASES_CONFIGURATION_SECTION, connectionName));
    }
}

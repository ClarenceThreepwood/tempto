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
package com.teradata.tempto.fulfillment.table;

import com.google.inject.Binder;
import com.google.inject.multibindings.MapBinder;

import java.util.Collection;

import static com.google.inject.multibindings.MapBinder.newMapBinder;
import static com.teradata.tempto.context.ThreadLocalTestContextHolder.testContext;

/**
 * Returns an appropriate {@link TableManager} based on table type.
 */
public interface TableManagerDispatcher
{
    <T extends TableDefinition> TableManager<T> getTableManagerFor(T tableDefinition);

    default <T extends TableDefinition> TableManager<T> getTableManagerFor(TableInstance<T> tableInstance)
    {
        return getTableManagerFor(tableInstance.tableDefinition());
    }

    Collection<TableManager> getAllTableManagers();

    public static TableManagerDispatcher getTableManagerDispatcher()
    {
        return testContext().getDependency(TableManagerDispatcher.class);
    }

    public static MapBinder<Class<? extends TableDefinition>, TableManager> tableManagerMapBinderFor(Binder binder)
    {
        return (MapBinder<Class<? extends TableDefinition>, TableManager>) newMapBinder(binder, TableDefinition.class.getClass(), TableManager.class);
    }
}

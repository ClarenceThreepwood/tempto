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
package com.teradata.tempto.internal.convention

import com.teradata.tempto.internal.convention.AnnotatedFileParser.SectionParsingResult
import spock.lang.Specification

import static com.google.common.collect.Iterables.getOnlyElement
import static com.teradata.tempto.fulfillment.table.MutableTableRequirement.State.CREATED
import static com.teradata.tempto.fulfillment.table.MutableTableRequirement.State.LOADED
import static java.util.Optional.empty
import static org.apache.commons.io.IOUtils.toInputStream

class SqlQueryDescriptorTest
        extends Specification
{
  def 'parses immutable table properties'()
  {
    setup:
    String fileContent = '-- tables: table1, prefix.table2'
    SectionParsingResult parsingResult = parseSection(fileContent)
    SqlQueryDescriptor queryDescriptor = new SqlQueryDescriptor(parsingResult)

    expect:
    queryDescriptor.tableDefinitionNames == [
            new TableName('table1', empty()),
            new TableName('table2', Optional.of('prefix'))
    ] as Set
  }

  def 'parses mutable table properties'()
  {
    setup:
    String fileContent = '-- mutable_tables: table1|loaded|table1_name, table2|created, table3, table4|created|prefix.table4_1'
    SectionParsingResult parsingResult = parseSection(fileContent)
    SqlQueryDescriptor queryDescriptor = new SqlQueryDescriptor(parsingResult)

    expect:
    queryDescriptor.mutableTableDescriptors.size() == 4

    queryDescriptor.mutableTableDescriptors[0].tableDefinitionName == 'table1'
    queryDescriptor.mutableTableDescriptors[0].state == LOADED;
    queryDescriptor.mutableTableDescriptors[0].name == new TableName('table1_name', empty())

    queryDescriptor.mutableTableDescriptors[1].tableDefinitionName == 'table2'
    queryDescriptor.mutableTableDescriptors[1].state == CREATED;
    queryDescriptor.mutableTableDescriptors[1].name == new TableName('table2', empty())

    queryDescriptor.mutableTableDescriptors[2].tableDefinitionName == 'table3'
    queryDescriptor.mutableTableDescriptors[2].state == LOADED;
    queryDescriptor.mutableTableDescriptors[2].name == new TableName('table3', empty())

    queryDescriptor.mutableTableDescriptors[3].tableDefinitionName == 'table4'
    queryDescriptor.mutableTableDescriptors[3].state == CREATED;
    queryDescriptor.mutableTableDescriptors[3].name == new TableName('table4_1', Optional.of('prefix'))
  }

  def 'should fail duplicate mutable table name'()
  {
    setup:
    String fileContent = '-- mutable_tables: table1, table1'
    SectionParsingResult parsingResult = parseSection(fileContent)
    SqlQueryDescriptor queryDescriptor = new SqlQueryDescriptor(parsingResult)

    when:
    queryDescriptor.mutableTableDescriptors

    then:
    def e = thrown(IllegalStateException)
    e.message == 'Table with name table1 is defined twice'
  }

  def parseSection(String content)
  {
    getOnlyElement(new AnnotatedFileParser().parseFile(toInputStream(content)))
  }
}

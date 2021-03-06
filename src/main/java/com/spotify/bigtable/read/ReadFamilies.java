/*-
 * -\-\-
 * simple-bigtable
 * --
 * Copyright (C) 2016 - 2017 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */

/*
 *
 *  * Copyright 2016 Spotify AB.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing,
 *  * software distributed under the License is distributed on an
 *  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  * KIND, either express or implied.  See the License for the
 *  * specific language governing permissions and limitations
 *  * under the License.
 *
 */

package com.spotify.bigtable.read;

import com.google.bigtable.v2.Family;
import com.google.bigtable.v2.Row;
import com.google.bigtable.v2.RowFilter;
import com.spotify.bigtable.read.ReadColumn.ColumnWithinFamiliesRead;
import com.spotify.bigtable.read.ReadColumn.ColumnWithinRowsRead;
import com.spotify.bigtable.read.ReadColumns.ColumnsWithinFamiliesRead;
import com.spotify.bigtable.read.ReadColumns.ColumnsWithinRowsRead;
import com.spotify.bigtable.read.ReadFamily.FamilyRead;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ReadFamilies {

  interface FamiliesRead<OneColT, MultiColT, R> extends FamilyRead<OneColT, MultiColT, R> { }

  public interface FamiliesWithinRowRead extends FamiliesRead<
      ColumnWithinFamiliesRead, ColumnsWithinFamiliesRead, List<Family>> {

    class ReadImpl
        extends AbstractFamiliesRead<
        FamiliesWithinRowRead.ReadImpl,
        ColumnWithinFamiliesRead,
        ColumnsWithinFamiliesRead,
        List<Family>,
        Optional<Row>>
        implements FamiliesWithinRowRead {

      ReadImpl(final Internal<Optional<Row>> parentRead) {
        super(parentRead);
      }

      @Override
      protected FamiliesWithinRowRead.ReadImpl multiFam() {
        return this;
      }

      @Override
      public ColumnWithinFamiliesRead columnQualifier(final String columnQualifier) {
        return new ColumnWithinFamiliesRead.ReadImpl(this).columnQualifier(columnQualifier);
      }

      @Override
      public ColumnsWithinFamiliesRead columnQualifierRegex(final String columnQualifierRegex) {
        return columns().columnQualifierRegex(columnQualifierRegex);
      }

      @Override
      public ColumnsWithinFamiliesRead.ReadImpl columns() {
        return new ColumnsWithinFamiliesRead.ReadImpl(this);
      }

      @Override
      protected Function<Optional<Row>, List<Family>> parentTypeToCurrentType() {
        return rowOpt -> rowOpt.map(Row::getFamiliesList).orElse(Collections.emptyList());
      }
    }
  }

  public interface FamiliesWithinRowsRead extends FamiliesRead<
      ColumnWithinRowsRead, ColumnsWithinRowsRead, List<Row>> {

    class ReadImpl
        extends AbstractFamiliesRead<
        FamiliesWithinRowsRead.ReadImpl,
        ColumnWithinRowsRead,
        ColumnsWithinRowsRead,
        List<Row>,
        List<Row>>
        implements FamiliesWithinRowsRead {

      ReadImpl(final Internal<List<Row>> parentRead) {
        super(parentRead);
      }

      @Override
      protected FamiliesWithinRowsRead.ReadImpl multiFam() {
        return this;
      }

      @Override
      public ColumnWithinRowsRead columnQualifier(final String columnQualifier) {
        return new ColumnWithinRowsRead.ReadImpl(this).columnQualifier(columnQualifier);
      }

      @Override
      public ColumnsWithinRowsRead columnQualifierRegex(final String columnQualifierRegex) {
        return columns().columnQualifierRegex(columnQualifierRegex);
      }

      @Override
      public ColumnsWithinRowsRead.ReadImpl columns() {
        return new ColumnsWithinRowsRead.ReadImpl(this);
      }

      @Override
      protected Function<List<Row>, List<Row>> parentTypeToCurrentType() {
        return Function.identity();
      }
    }
  }

  private abstract static class AbstractFamiliesRead<MultiFamT, OneColT, MultiColT, R, P>
      extends AbstractBigtableRead<P, R> implements FamiliesRead<OneColT, MultiColT, R> {

    private AbstractFamiliesRead(final Internal<P> parentRead) {
      super(parentRead);
    }

    protected abstract MultiFamT multiFam();

    MultiFamT familyRegex(final String columnFamilyRegex) {
      final RowFilter.Builder familyFilter = RowFilter.newBuilder()
          .setFamilyNameRegexFilter(columnFamilyRegex);
      addRowFilter(familyFilter);
      return multiFam();
    }

    @Override
    public MultiColT columnsQualifiers(Collection<String> columnQualifiers) {
      return columnQualifierRegex(toExactMatchAnyRegex(columnQualifiers));
    }
  }
}

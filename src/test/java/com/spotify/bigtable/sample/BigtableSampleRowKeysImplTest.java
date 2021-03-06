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

package com.spotify.bigtable.sample;

import com.google.bigtable.v2.SampleRowKeysRequest;
import com.spotify.bigtable.BigtableMock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class BigtableSampleRowKeysImplTest {

  BigtableMock bigtableMock = BigtableMock.getMock();
  BigtableSampleRowKeysImpl bigtableSampleRowKeys;

  @Before
  public void setUp() throws Exception {
    bigtableSampleRowKeys = new BigtableSampleRowKeysImpl(bigtableMock, "table");
  }

  private void testSampleRowKeys() throws Exception {
    final SampleRowKeysRequest.Builder sampleRowKeys = bigtableSampleRowKeys.getSampleRowKeysRequest();
    assertEquals(bigtableMock.getFullTableName("table"), sampleRowKeys.getTableName());
  }

  @Test
  public void testExecute() throws Exception {
    testSampleRowKeys();
    bigtableSampleRowKeys.execute();
    verify(bigtableMock.getMockedDataClient()).sampleRowKeys(bigtableSampleRowKeys.getSampleRowKeysRequest().build());
    verifyNoMoreInteractions(bigtableMock.getMockedDataClient());
  }

  @Test
  public void testExecuteAsync() throws Exception {
    testSampleRowKeys();
    bigtableSampleRowKeys.executeAsync();
    verify(bigtableMock.getMockedDataClient()).sampleRowKeysAsync(bigtableSampleRowKeys.getSampleRowKeysRequest().build());
    verifyNoMoreInteractions(bigtableMock.getMockedDataClient());
  }
}

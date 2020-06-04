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
package com.facebook.presto.plugin.geospatial;

import com.facebook.presto.operator.scalar.AbstractTestFunctions;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.facebook.presto.plugin.geospatial.BingTile.MAX_ZOOM_LEVEL;
import static com.facebook.presto.plugin.geospatial.BingTile.fromCoordinates;
import static org.testng.Assert.assertEquals;

public class TestBingTile
        extends AbstractTestFunctions
{
    @Test
    public void testSerialization()
            throws Exception
    {
        ObjectMapper objectMapper = new ObjectMapper();
        BingTile tile = fromCoordinates(1, 2, 3);
        String json = objectMapper.writeValueAsString(tile);
        assertEquals("{\"x\":1,\"y\":2,\"zoom\":3}", json);
        assertEquals(tile, objectMapper.readerFor(BingTile.class).readValue(json));
    }

    @Test
    public void testBingTileEncoding()
    {
        for (int zoom = 0; zoom <= MAX_ZOOM_LEVEL; zoom++) {
            int maxValue = (1 << zoom) - 1;
            testEncodingRoundTrip(0, 0, zoom);
            testEncodingRoundTrip(0, maxValue, zoom);
            testEncodingRoundTrip(maxValue, 0, zoom);
            testEncodingRoundTrip(maxValue, maxValue, zoom);
        }
    }

    private void testEncodingRoundTrip(int x, int y, int zoom)
    {
        BingTile expected = BingTile.fromCoordinates(x, y, zoom);
        BingTile actual = BingTile.decode(expected.encode());
        assertEquals(actual, expected);
    }
}

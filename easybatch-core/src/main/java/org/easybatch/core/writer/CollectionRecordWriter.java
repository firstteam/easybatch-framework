/**
 * The MIT License
 *
 *   Copyright (c) 2017, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.easybatch.core.writer;

import org.easybatch.core.record.Batch;
import org.easybatch.core.record.Record;

import java.util.Collection;

import static org.easybatch.core.util.Utils.checkNotNull;

/**
 * Convenient processor that writes records to a {@link java.util.Collection}.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class CollectionRecordWriter implements RecordWriter {

    private Collection collection;

    /**
     * Convenient processor that writes records to a {@link java.util.Collection}.
     *
     * @param collection the collection to write records to
     */
    public CollectionRecordWriter(final Collection collection) {
        checkNotNull(collection, "collection");
        this.collection = collection;
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    @SuppressWarnings(value = "unchecked")
    public void writeRecords(Batch batch) throws Exception {
        for (Record record : batch) {
            collection.add(record.getPayload());
        }
    }

    @Override
    public void close() throws Exception {

    }
}

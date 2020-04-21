/*
   Copyright (c) 2020 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package com.linkedin.data.codec.entitystream;

import com.linkedin.data.Data;
import com.linkedin.data.DataList;
import com.linkedin.data.DataMap;
import com.linkedin.data.codec.ProtobufDataCodec;
import com.linkedin.data.codec.symbol.EmptySymbolTable;
import com.linkedin.data.codec.symbol.SymbolTable;
import com.linkedin.data.protobuf.ProtoWriter;
import java.io.IOException;
import java.io.OutputStream;


/**
 * LI Protobuf encoder for a {@link com.linkedin.data.DataComplex} object implemented
 * as a {@link com.linkedin.entitystream.Writer} writing to an {@link com.linkedin.entitystream.EntityStream} of
 * {@link com.linkedin.data.ByteString}. The generator writes to an internal non-blocking <code>OutputStream</code>
 * implementation that has a fixed-size primary buffer and an unbounded overflow buffer. Because the bytes are pulled
 * from the encoder asynchronously, it needs to keep the state in a stack.
 *
 * <p>This is a tweaked version of JSON that serializes maps as lists, and has support for serializing field IDs
 * in lieu of field names using an optional symbol table. The payload is serialized using {@link ProtoWriter}</p>
 *
 * @author amgupta1
 */
public class ProtobufDataEncoder extends AbstractDataEncoder
{
  private final SymbolTable _symbolTable;
  private ProtoWriter _writer;

  public ProtobufDataEncoder(DataMap dataMap, int bufferSize)
  {
    this(dataMap, bufferSize, null);
  }

  public ProtobufDataEncoder(DataList dataList, int bufferSize)
  {
    this(dataList, bufferSize, null);
  }

  public ProtobufDataEncoder(DataMap dataMap, int bufferSize, SymbolTable symbolTable)
  {
    super(dataMap, bufferSize);
    _symbolTable = symbolTable == null ? EmptySymbolTable.SHARED : symbolTable;
  }

  public ProtobufDataEncoder(DataList dataList, int bufferSize, SymbolTable symbolTable)
  {
    super(dataList, bufferSize);
    _symbolTable = symbolTable == null ? EmptySymbolTable.SHARED : symbolTable;
  }

  @Override
  protected Data.TraverseCallback initTraverseCallback(OutputStream out) throws IOException
  {
    _writer = new ProtoWriter(out);
    return new ProtobufDataCodec.ProtobufTraverseCallback(_writer, _symbolTable);
  }

  @Override
  protected void flushOutputStream(Data.TraverseCallback traverseCallback) throws IOException
  {
    _writer.close();
  }
}

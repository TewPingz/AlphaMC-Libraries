// This file was designed and is an original Plugin for AlphaMC
// Copyright (C) 2021 Foxtrot LLC
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.

package rip.alpha.libraries.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;
import rip.alpha.libraries.json.GsonProvider;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GsonDecoder implements Decoder<Object> {

    private final Map<String, Class<?>> classMap = new ConcurrentHashMap<>();

    @Override
    public Object decode(ByteBuf buf, State state) throws IOException {
        try (ByteBufInputStream stream = new ByteBufInputStream(buf)) {
            String string = stream.readUTF();
            String type = stream.readUTF();

            Class<?> clazz = this.getClassFromType(type);

            if (clazz == null) {
                return null;
            }

            return GsonProvider.fromJson(string, clazz);
        }
    }

    private Class<?> getClassFromType(String name) {
        Class<?> clazz = this.classMap.get(name);

        if (clazz == null) {
            try {
                clazz = Class.forName(name);
                this.classMap.put(name, clazz);
            } catch (ClassNotFoundException e) {
                return null;
            }
        }

        return clazz;
    }
}

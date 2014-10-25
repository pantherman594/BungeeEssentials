/*
 * Copyright (c) 2014 Connor Spencer Harries
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.hadrondev.commands;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import me.hadrondev.BungeeEssentials;
import me.hadrondev.permissions.Permission;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by Connor Harries on 25/10/2014.
 */
public class Dispatch extends Command {
    public Dispatch() {
        super("dispatch", Permission.ADMIN_DISPATCH.toString());
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        String send = Arrays.toString(args);

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Dispatch");

        ByteArrayOutputStream messageBytes = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(messageBytes);

        try {
            stream.writeUTF(send);
        } catch (IOException e) {
            BungeeEssentials.me.getLogger().severe("Unable to write command to stream");
            return;
        }

        byte[] bytes = messageBytes.toByteArray();

        out.writeShort(bytes.length);
        out.write(bytes);

        bytes = out.toByteArray();

        BungeeEssentials.me.getLogger().info("Sent command via Dispatch channel");
        for(ServerInfo server : BungeeEssentials.me.getProxy().getServers().values()) {
            server.sendData("BungeeEssentials", bytes);
        }
    }
}

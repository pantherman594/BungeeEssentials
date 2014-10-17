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

import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Connor Harries on 17/10/2014.
 */
public enum CommandRegister {
    ALERT("alert", new Alert()),
    DISPATCH("dispatch", new Dispatch()),
    FIND("find", new Find()),
    LIST("list", new ServerList()),
    MESSAGE("message", new Message()),
    REPLY("message", new Reply()),
    SEND("send", new Send()),
    SEND_ALL("sendall", new SendAll()),
    SLAP("slap", new Slap());

    private final Command command;
    private final String name;

    private CommandRegister(String name, Command command) {
        this.name = name;
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public String getName() {
        return this.name;
    }
}

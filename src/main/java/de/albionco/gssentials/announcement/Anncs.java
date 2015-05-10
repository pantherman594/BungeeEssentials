/*
 * Copyright (c) 2015 Connor Spencer Harries
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

package de.albionco.gssentials.announcement;

import com.google.common.base.Preconditions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David on 5/8/2015.
 *
 * @author David Shen
 */
public class Anncs {
    public static HashMap<Integer, Integer> annc_Delay = new HashMap<>();
    public static HashMap<Integer, Integer> annc_Interval = new HashMap<>();
    public static HashMap<Integer, String> annc_Msg = new HashMap<>();

    public static void deserialize(Map<String, String> serialized) {
        int size = annc_Interval.size();
        Preconditions.checkNotNull(serialized);
        Preconditions.checkArgument(!serialized.isEmpty());
        Preconditions.checkNotNull(serialized.get("delay"), "invalid delay");
        Preconditions.checkNotNull(serialized.get("interval"), "invalid interval");
        Preconditions.checkNotNull(serialized.get("message"), "invalid message");

        size++;
        annc_Delay.put(size, Integer.parseInt(String.valueOf(serialized.get("delay"))));
        annc_Interval.put(size, Integer.parseInt(String.valueOf(serialized.get("interval"))));
        annc_Msg.put(size, String.valueOf(serialized.get("message")));
    }
}
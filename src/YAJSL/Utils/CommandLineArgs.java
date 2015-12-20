/* 
 * YAJSL - Yet Another Java Swing Library
 *
 * Copyright (c) 2013 Giuseppe Gallo
 *
 * LICENSED UNDER:
 *
 *  The MIT License (MIT)
 *
 *  Copyright (c) 2013 Giuseppe Gallo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */
package YAJSL.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Command-line arguments parser similar to getopt.
 * Accepts in input the array of arguments and a string which configures the
 * valid command line options.<p>
 * The string is a repetition of the following pattern:<p>
 * {@code    <option_letter>[{:|!}]}<p>
 * <p>
 * Where:<p>
 * ":" indicates that the option is optional and that it requires a parameter.<p>
 * "!" indicates that the option is mandatory and that it requires a parameter.<p>
 * <p>
 * Please note that it does not have any sense to have mandatory options not requiring a parameter.<p>
 * <p>
 * Exceptions are thrown for any invalid configuration:<p>
 * <ul>
 * <li>Unknown options passed</li>
 * <li>Parameters not passed for options requiring a parameter</li>
 * <li>Missing mandatory options</li>
 * </ul>
 * <p>
 * Any additional parameter (not corresponding to any option) is collected separately.<p>
 * <p>
 * Methods {@link #hasOption}, {@link #getOption} and {@link #getParams} can be used to
 * interrogate parsed options and parameters.
 * 
 * @author Giuseppe Gallo
 */
public class CommandLineArgs {
    /** Flag to indicate that a option requires a parameter. */
    private final static int FLAG_VALUE = 1;

    /** Flag to indicate that a option is mandatory and requires a parameter. */
    private final static int FLAG_REQUIRED = 2;

    /** The HashMap with all options passed and the corresponding parameters (if any). */
    protected HashMap<String, String> options = null;

    /** The list of all parameters not part of the options. */
    protected LinkedList<String> params = null;

    /**
     * Default constructor.
     */
    public CommandLineArgs() {}

    /**
     * Create an instance of the command-line parser based on a list of arguments and
     * the configuration string.<p>
     * Using this constructor is equivalent to using the default constructor and then
     * method {@link #parseArgs}.
     *
     * @param args    the array of command-line arguments to be analyzed
     * @param valid   the string configuring the valid options
     * @throws Exception   in case of any parsing error
     */
    public CommandLineArgs(String[] args, String valid) throws Exception {
        parseArgs(args, valid);
    }

    /**
     * Parses the given list of arguments based on the supplied configuration string.
     *
     * @param args    the array of command-line arguments to be analyzed
     * @param valid   the string configuring the valid options
     * @throws Exception   in case of any parsing error
     */
    private void parseArgs(String[] args, String valid) throws Exception {
        options = new HashMap<>();
        params = new LinkedList<>();
        HashMap<String, Integer> validator = buildValidator(valid);
        String msg = "";

        /* First parsing phase: collect all options and parameters
         * and trigger exceptions for unknown options */
        String key = null;
        for (int i = 0; i < args.length; ++i) {
            String s = args[i];
            if (s.startsWith("-")) {
                key = s;
                if (!validator.containsKey(key)) {
                    msg += "Invalid option " + key + ".\n";
                } else {
                    options.put(key, null);
                }
            } else if (key != null && ((validator.get(key) & FLAG_VALUE) != 0)) {
                options.put(key, s);
                key = null;
            } else {
                params.add(s);
            }
        }

        /* Second parsing phase: trigger exceptions for missing
         * values for options requiring one */
        for (String opt : options.keySet()) {
            String val = options.get(opt);
            if (val == null && (validator.get(opt) & FLAG_VALUE) != 0) {
                msg += "Option " + opt + " requires a value.\n";
            }
        }

        /* Third parsing phase: trigger exceptions for missing
         * mandatory options */
        for (String opt : validator.keySet()) {
            if ((validator.get(opt) & FLAG_REQUIRED) != 0 && !options.containsKey(opt)) {
                msg += "Option " + opt + " is mandatory.\n";
            }
        }

        if (!msg.equals("")) {
            throw new Exception(msg);
        }
    }

    /**
     * Parses the string which configures the valid options and build a HashMap
     * to be used when command-line arguments will be analyzed.
     * The HashMap returned associates to each acceptable option a flag indicating
     * the type:<p>
     * <ul>
     * <li>{@link #FLAG_VALUE}: if the bit is set, the option requires a value</li>
     * <li>{@link #FLAG_REQUIRED}: if the bit is set, the option is mandatory and requires a value</li>
     * </ul>
     *
     * @param valid   the string configuring the valid options
     * @return   the HashMap with all supported options
     */
    private HashMap<String, Integer> buildValidator(String valid) {
        char option = 0;
        HashMap<String, Integer> validator = new HashMap<>();
        for (int i = 0; i < valid.length(); ++i) {
            char c = valid.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z')) {
                option = c;
                String opt = "-" + String.valueOf(option);
                validator.put(opt, 0);
            } else if (c == ':' && option != 0) {
                String opt = "-" + String.valueOf(option);
                Integer value = validator.get(opt);
                validator.put(opt, value | FLAG_VALUE );
            } else if (c == '!' && option != 0) {
                String opt = "-" + String.valueOf(option);
                Integer value = validator.get(opt);
                validator.put(opt, value | FLAG_REQUIRED | FLAG_VALUE);
            } else {
                option = 0;
            }
        }
        return validator;
    }

    /**
     * Returns the parameter passed to the given option, or null if the option was not
     * passed or it does not have any parameter.<p>
     *
     * @param opt   the option string, inclusive of the "-", for instance, "-h"
     * @return   the parameter passed to the given option, or null if the option was not
     *           passed or it does not have any parameter
     */
    public String getOption(String opt) {
        if (!hasOption(opt)) return null;
        String value = options.get(opt);
        return (value == null) ? "" : value;
    }

    /**
     * Returns {@code true} if the given option has been passed, {@code false} otherwise.
     * 
     * @param opt   the option string, inclusive of the "-", for instance, "-h"
     * @return   {@code true} if the given option has been passed, {@code false} otherwise
     */
    public boolean hasOption(String opt) {
        return options.containsKey(opt);
    }

    /**
     * Returns the list of parameters not pertaining to any option.
     *
     * @return   the list of parameters not pertaining to any option
     */
    public final List<String> getParams() {
        return params;
    }
}

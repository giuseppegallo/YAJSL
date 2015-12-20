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
package YAJSL.Data;

import YAJSL.Utils.Localizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import javax.swing.Icon;

/**
 * Represents a supported locale.
 * 
 * @author Giuseppe Gallo
 */
public class Language extends Localizer.Language implements Listable {
    
    /** The list of supported languages */
    private static ArrayList<Language> languages = null;
    
    /** The supported languages organized by language tag */
    private static HashMap<String, Language> langsByTag = null;
    
    
    /**
     * Creates a new Language instance.
     * 
     * @param locale  the locale corresponding to the language
     * @param name  the name to be shown for the language
     * @param icon  the icon to be shown for the language
     */
    public Language(Locale locale, String name, Icon icon) {
        super(locale, name, icon);
    }

    /**
     * Creates a new Language instance.
     * 
     * @param lang  the localizer language
     */
    public Language(Localizer.Language lang) {
        super(lang.getLocale(), lang.getName(), lang.getIcon());
    }
    
    /**
     * Returns the language tag for this language.
     * 
     * @return  the language tag for this language
     */
    public String getTag() {
       return locale.toLanguageTag();
    }
    
    /**
     * Initializes languages supported by the given localizer.
     * 
     * @param loc  the localizer
     */
    public static void init(Localizer loc) {
        Collection<Localizer.Language> langs = loc.getLanguages();
        
        languages = new ArrayList<>(langs.size());
        langsByTag = new HashMap<>(langs.size());
        
        loc.getLanguages().forEach(lang -> {
            Language l = new Language(lang);
            languages.add(l);
            langsByTag.put(l.getTag(), l);
        });
    }
    
    /**
     * Returns the list of languages supported.
     * 
     * @return  the list of languages supported
     */
    public static ArrayList<Language> getLanguages() {
        return languages;
    }
    
    /**
     * Returns the language corresponding to the given tag.
     * 
     * @param tag  the language tag
     * @return  the language corresponding to the given tag
     */
    public static Language findByTag(String tag) {
        return langsByTag.get(tag);
    }
    
    @Override
    public Icon getListableIcon() {
        return icon;
    }

    @Override
    public String getListableText() {
        return name;
    }
}

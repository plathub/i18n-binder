package org.omnaest.i18nbinder.internal.facade.i18nfacade.i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import javax.annotation.Generated;
import org.omnaest.i18nbinder.internal.facade.i18nfacade.i18n.sub1.Shared;

/**
 * This is an automatically with i18nBinder generated facade class.<br><br>
 * To modify please adapt the underlying property files.<br><br>
 * If the facade class is instantiated with a given {@link Locale} using {@link #Sub1(Locale)} all non static methods will use this predefined {@link Locale} when invoked.<br><br>
 * The facade methods will silently ignore all {@link MissingResourceException}s by default. To alter this behavior see {@link #Sub1(Locale, boolean)}<br><br>
 * Resource base: <b>i18n.sub1</b>
 * @see Shared
 */
@Generated(value = "https://github.com/schlothauer-wauer/i18n-binder/")
public class Sub1
{
	/** @see Shared */
	public final Shared Shared;

	/**
	 * This {@link Sub1} constructor will create a new instance which silently ignores any {@link MissingResourceException}
	 * @param locale
	 * @see Sub1
	 */
	public Sub1(Locale locale)
	{
		this(locale, true);
	}

	/**
	 * @param locale
	 * @param silentlyIgnoreMissingResourceException
	 * @see Sub1
	 */
	public Sub1(Locale locale, boolean silentlyIgnoreMissingResourceException)
	{
		super();
		this.Shared = new Shared(locale, silentlyIgnoreMissingResourceException);
	}

}


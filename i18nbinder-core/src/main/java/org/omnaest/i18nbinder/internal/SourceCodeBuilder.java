package org.omnaest.i18nbinder.internal;

import org.apache.commons.lang3.StringUtils;

public class SourceCodeBuilder
{
	private StringBuilder stringBuilder;

	private int indention = 0;

	public SourceCodeBuilder(StringBuilder strBuilder)
	{
		this.stringBuilder = strBuilder;
	}

	public SourceCodeBuilder append(String str)
	{
		stringBuilder.append(getIndention() + str + "\n");
		return this;
	}

	public SourceCodeBuilder beginBlock()
	{
		stringBuilder.append(getIndention() + "{" + "\n");
		indention++;
		return this;
	}

	/** Begins a new block after <code>beginWith</code> */
	public SourceCodeBuilder begins(String beginWith)
	{
		return append(beginWith).beginBlock();
	}

	public JavaDocBuilder javaDoc(String... strings)
	{
		append("/**");
		JavaDocBuilder javaDocBuilder = new JavaDocBuilder(this);
		for (String str : strings)
		{
			javaDocBuilder.append(str);
		}
		return javaDocBuilder;
	}

	public SourceCodeBuilder endBlock()
	{
		return endBlock("");
	}

	public SourceCodeBuilder endBlock(String str)
	{
		indention--;
		stringBuilder.append(getIndention() + "}" + str + "\n");
		return this;
	}

	public SourceCodeBuilder newLine()
	{
		stringBuilder.append("\n");
		return this;
	}

	protected String getIndention()
	{
		return StringUtils.repeat("\t", indention);
	}

	public int length()
	{
		return stringBuilder.length();
	}

	public SourceCodeBuilder returns(String returnStr)
	{
		append("return " + returnStr);
		return this;
	}

	@Override
	public String toString()
	{
		return stringBuilder.toString();
	}

	public class JavaDocBuilder
	{
		private SourceCodeBuilder codeBuilder;

		public JavaDocBuilder(SourceCodeBuilder codeBuilder)
		{
			this.codeBuilder = codeBuilder;
		}

		public JavaDocBuilder params(String... params)
		{
			for (String param : params)
			{
				param(param);
			}
			return this;
		}

		public JavaDocBuilder param(String param)
		{
			codeBuilder.append(" * @param " + param);
			return this;
		}

		public JavaDocBuilder returns(String returnStr)
		{
			codeBuilder.append(" * @return " + returnStr);
			return this;
		}

		public JavaDocBuilder see(String reference)
		{
			codeBuilder.append(" * @see " + reference);
			return this;
		}

		public JavaDocBuilder append(String str)
		{
			codeBuilder.append(" * " + str);
			return this;
		}

		public void end()
		{
			codeBuilder.append(" */");
		}
	}
}

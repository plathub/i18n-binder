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

	public SourceCodeBuilder endBlock()
	{
		indention--;
		stringBuilder.append(getIndention() + "}" + "\n");
		return this;
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

	@Override
	public String toString()
	{
		return stringBuilder.toString();
	}
}

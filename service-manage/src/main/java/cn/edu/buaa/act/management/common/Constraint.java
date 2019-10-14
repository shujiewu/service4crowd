package cn.edu.buaa.act.management.common;


import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Constraint {

	private static final Pattern PARSE_REGEX = Pattern.compile("(?<field>[^ ]+) (?<op>[^ ]+)( (?<param>.+))?");

	private final String field;

	private final String operator;

	private final String parameter;

	public Constraint(String raw) {
		Matcher matcher = PARSE_REGEX.matcher(raw);
		Assert.isTrue(matcher.matches(), "Could not parse [" + raw + "] as a Marathon constraint (field operator param?)");
		this.field = matcher.group("field");
		this.operator = matcher.group("op");
		this.parameter = matcher.group("param"); // may be null
	}

	public List<String> toStringList() {
		if (this.parameter != null) {
			return Arrays.asList(this.field, this.operator, this.parameter);
		}
		else {
			return Arrays.asList(this.field, this.operator);
		}
	}
}

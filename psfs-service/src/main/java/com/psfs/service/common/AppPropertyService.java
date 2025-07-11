package com.psfs.service.common;

import java.io.StringWriter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.ConversionTool;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.FieldTool;
import org.apache.velocity.tools.generic.LoopTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.ResourceTool;
import org.apache.velocity.tools.generic.XmlTool;

@Component
public class AppPropertyService {
	private static final Log LOG = LogFactory.getLog(AppPropertyService.class);

	public static String getProperty(String key) {
		Environment enviroinment = AppContext.getBean(Environment.class);

		if (enviroinment != null) {
			String value = enviroinment.getProperty(key);
			return value;
		}

		return null;

	}

	public static String getProperty(String key, String defaultValue) {
		Environment enviroinment = AppContext.getBean(Environment.class);

		if (enviroinment != null) {
			String value = enviroinment.getProperty(key, defaultValue);
			return value;
		}

		return defaultValue;

	}

	public static <T extends Object> T getProperty(String key, Class<T> classType, T defaultValue) {
		Environment enviroinment = AppContext.getBean(Environment.class);

		if (enviroinment != null) {
			T value = enviroinment.getProperty(key, classType, defaultValue);
			return value;
		}

		return defaultValue;

	}

	public static String getProperty(String key, Map<String, Object> args) {
		Environment enviroinment = AppContext.getBean(Environment.class);

		if (enviroinment != null) {

			String value = enviroinment.getProperty(key);

			if (StringUtils.isNotEmpty(value) && args != null) {
				return getFormattedMessage(args, value);
			} else {
				return value;
			}
		}

		return null;
	}

	private static String getFormattedMessage(Map<String, Object> args, String templateString) {
		StringWriter output = new StringWriter();

		try {

			VelocityContext context = new VelocityContext();
			context.put("conversionTool", new ConversionTool());
			context.put("dateTool", new DateTool());
			context.put("comparisonDateTool", new ComparisonDateTool());
			context.put("displayTool", new DisplayTool());
			context.put("escapeTool", new EscapeTool());
			context.put("fieldTool", new FieldTool());
			context.put("loopTool", new LoopTool());
			context.put("mathTool", new MathTool());
			context.put("numberTool", new NumberTool());
			context.put("resourceTool ", new ResourceTool());
			context.put("xmlTool ", new XmlTool());
			context.put("stringUtils ", StringUtils.class);

			Set<Entry<String, Object>> entrySet = args.entrySet();

			// Populate VelocityContext using the key-value pair
			for (Entry<String, Object> entry : entrySet) {

				String key = entry.getKey();
				Object value = entry.getValue();

				if (StringUtils.isNotEmpty(key) && value != null) {
					context.put(key, value);
				}
			}

			if (StringUtils.endsWith(templateString, ".vm")) {
				Velocity.mergeTemplate(templateString, "UTF-8", context, output);
			} else {
				Velocity.evaluate(context, output, "", templateString);
			}

		} catch (ParseErrorException e) {
			LOG.error("ParseErrorException:" + e.getMessage());
		} catch (MethodInvocationException e) {
			LOG.error("MethodInvocationException:" + e.getMessage());
		} catch (ResourceNotFoundException e) {
			LOG.error("ResourceNotFoundException:" + e.getMessage());
		}

		return output.toString();
	}

}

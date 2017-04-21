/*
 * Copyright (C) 2016 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fraunhofer.iosb.ilt.sta.util;

import de.fraunhofer.iosb.ilt.sta.model.FeatureOfInterest;
import de.fraunhofer.iosb.ilt.sta.model.builder.FeatureOfInterestBuilder;
import de.fraunhofer.iosb.ilt.sta.model.builder.ObservationBuilder;
import de.fraunhofer.iosb.ilt.sta.model.ext.TimeInstant;
import de.fraunhofer.iosb.ilt.sta.model.ext.TimeInterval;
import de.fraunhofer.iosb.ilt.sta.model.ext.TimeValue;
import de.fraunhofer.iosb.ilt.sta.model.id.LongId;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author scf
 */
public class ArrayValueHandlers {

    /**
     * The logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayValueHandlers.class);

    public interface ArrayValueHandler {

        public void handle(Object value, ObservationBuilder target);
    }
    /**
     * Our default handlers.
     */
    private static Map<String, ArrayValueHandler> HANDLERS;

    public static ArrayValueHandler getHandler(String component) {
        if (HANDLERS == null) {
            createDefaults();
        }
        return HANDLERS.get(component);
    }

    private static synchronized void createDefaults() {
        HANDLERS = new HashMap<>();
        ArrayValueHandler idHandler = (Object value, ObservationBuilder target) -> {
            if (value instanceof Long) {
                target.setId(new LongId((Long) value));
                return;
            }
            if (value instanceof Integer) {
                target.setId(new LongId((Integer) value));
                return;
            }
            throw new IllegalArgumentException("Only int or long allowed for ID.");
        };
        HANDLERS.put("id", idHandler);
        HANDLERS.put("@iot.id", idHandler);
        HANDLERS.put("result", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            target.setResult(value);
        });
        HANDLERS.put("resultQuality", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            target.setResultQuality(value);
        });
        HANDLERS.put("parameters", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            if (value instanceof Map) {
                target.setParameters((Map<String, Object>) value);
                return;
            }
            throw new IllegalArgumentException("parameters has to be a map.");
        });
        HANDLERS.put("phenomenonTime", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            try {
                TimeInstant time = TimeInstant.parse(value.toString());
                target.setPhenomenonTime(time);
                return;
            } catch (Exception e) {
                LOGGER.trace("Not a time instant: {}.", value);
            }
            try {
                TimeInterval time = TimeInterval.parse(value.toString());
                target.setPhenomenonTime(time);
                return;
            } catch (Exception e) {
                LOGGER.trace("Not a time interval: {}.", value);
            }
            throw new IllegalArgumentException("phenomenonTime could not be parsed as time instant or time interval.");
        });
        HANDLERS.put("resultTime", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            try {
                TimeInstant time = TimeInstant.parse(value.toString());
                target.setResultTime(time);
            } catch (Exception e) {
                throw new IllegalArgumentException("resultTime could not be parsed as time instant or time interval.", e);
            }
        });
        HANDLERS.put("validTime", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            try {
                TimeInterval time = TimeInterval.parse(value.toString());
                target.setValidTime(time);
            } catch (Exception e) {
                throw new IllegalArgumentException("resultTime could not be parsed as time instant or time interval.", e);
            }
        });
        HANDLERS.put("FeatureOfInterest/id", (ArrayValueHandler) (Object value, ObservationBuilder target) -> {
            FeatureOfInterestBuilder foiBuilder = new FeatureOfInterestBuilder();
            if (value instanceof Long) {
                target.setFeatureOfInterest(foiBuilder.setId(new LongId((Long) value)).build());
                return;
            }
            if (value instanceof Integer) {
                target.setFeatureOfInterest(foiBuilder.setId(new LongId((Integer) value)).build());
                return;
            }
            throw new IllegalArgumentException("Only int or long allowed for ID.");
        });

    }
}

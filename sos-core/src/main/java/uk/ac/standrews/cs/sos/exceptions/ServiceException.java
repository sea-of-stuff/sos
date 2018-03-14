/*
 * Copyright 2018 Systems Research Group, University of St Andrews:
 * <https://github.com/stacs-srg>
 *
 * This file is part of the module core.
 *
 * core is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * core is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with core. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package uk.ac.standrews.cs.sos.exceptions;

/**
 * @author Simone I. Conte "sic2@st-andrews.ac.uk"
 */
public class ServiceException extends SOSException {

    public ServiceException(SERVICE service, String message) {
        super(service.toString() + message);
    }

    public ServiceException(SERVICE service, String message, Throwable throwable) {
        super(service.toString() + message + " --- " + throwable.getMessage(), throwable);
    }

    public ServiceException(SERVICE service, Throwable throwable) {
        super(service.toString() + throwable.getMessage(), throwable);
    }

    public enum SERVICE {
        AGENT("AGENT"),
        CONTEXT("CONTEXT"),
        MANIFESTS_DATA("MANIFESTS_DATA"),
        METADATA("METADATA"),
        NODE("NODE"),
        STORAGE("STORAGE"),
        USRO("USRO");

        private String service;
        SERVICE(String service) {
            this.service = service;
        }

        @Override
        public String toString() {
            return service + " -- ";
        }
    }
}
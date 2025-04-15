/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package adc.dxp.service.builder.service;

import com.liferay.osgi.util.service.Snapshot;

import java.util.List;

/**
 * Provides the local service utility for EventResource. This utility wraps
 * <code>adc.dxp.service.builder.service.impl.EventResourceLocalServiceImpl</code> and
 * is an access point for service operations in application layer code running
 * on the local server. Methods of this service will not have security checks
 * based on the propagated JAAS credentials because this service can only be
 * accessed from within the same VM.
 *
 * @author Brian Wing Shun Chan
 * @see EventResourceLocalService
 * @generated
 */
public class EventResourceLocalServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>adc.dxp.service.builder.service.impl.EventResourceLocalServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */
	public static List<com.liferay.calendar.model.CalendarBooking>
		getCalendarBookingUpcoming() {

		return getService().getCalendarBookingUpcoming();
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	public static EventResourceLocalService getService() {
		return _serviceSnapshot.get();
	}

	private static final Snapshot<EventResourceLocalService> _serviceSnapshot =
		new Snapshot<>(
			EventResourceLocalServiceUtil.class,
			EventResourceLocalService.class);

}
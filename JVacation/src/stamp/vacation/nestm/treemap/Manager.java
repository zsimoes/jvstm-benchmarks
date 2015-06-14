package stamp.vacation.nestm.treemap;

import jvstm.TransactionSignaller;


/* =============================================================================
 *
 * manager.c
 * -- Travel reservation resource manager
 *
 * =============================================================================
 *
 * Copyright (C) Stanford University, 2006.  All Rights Reserved.
 * Author: Chi Cao Minh
 *
 * =============================================================================
 *
 * For the license of bayes/sort.h and bayes/sort.c, please see the header
 * of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of kmeans, please see kmeans/LICENSE.kmeans
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of ssca2, please see ssca2/COPYRIGHT
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/mt19937ar.c and lib/mt19937ar.h, please see the
 * header of the files.
 * 
 * ------------------------------------------------------------------------
 * 
 * For the license of lib/rbtree.h and lib/rbtree.c, please see
 * lib/LEGALNOTICE.rbtree and lib/LICENSE.rbtree
 * 
 * ------------------------------------------------------------------------
 * 
 * Unless otherwise noted, the following license applies to STAMP files:
 * 
 * Copyright (c) 2007, Stanford University
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 * 
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in
 *       the documentation and/or other materials provided with the
 *       distribution.
 * 
 *     * Neither the name of Stanford University nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY STANFORD UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL STANFORD UNIVERSITY BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 *
 * =============================================================================
 */

/* =============================================================================
 * DECLARATION OF TM_CALLABLE FUNCTIONS
 * =============================================================================
 */
public class Manager {
    final TreeMap<Integer, Reservation> carTable;
    final TreeMap<Integer, Reservation> roomTable;
    final TreeMap<Integer, Reservation> flightTable;
    final TreeMap<Integer, Customer> customerTable;

    public Manager() {
	carTable = new TreeMap<Integer, Reservation>();
	roomTable = new TreeMap<Integer, Reservation>();
	flightTable = new TreeMap<Integer, Reservation>();
	customerTable = new TreeMap<Integer, Customer>();
    }

    boolean addReservation(TreeMap<Integer, Reservation> table, int id, int num, int price) {
	Reservation reservation;

	reservation = table.get(id);
	if (reservation == null) {
	    /* Create new reservation */
	    if (num < 1 || price < 0) {
		return false;
	    }
	    reservation = new Reservation(id, num, price);
	    table.put(id, reservation);
	} else {
	    /* Update existing reservation */
	    if (!reservation.reservation_addToTotal(num)) {
		return false;
	    }
	    if (reservation.numTotal.get() == 0) {
		boolean status = table.remove(id) != null ? true : false;
		if (!status) {
		    TransactionSignaller.SIGNALLER.signalCommitFail();
		}
	    } else {
		reservation.reservation_updatePrice(price);
	    }
	}

	return true;
    }

    /*
     * ==========================================================================
     * === manager_addCar -- Add cars to a city -- Adding to an existing car
     * overwrite the price if 'price' >= 0 -- Returns TRUE on success, else
     * FALSE
     * ====================================================================
     * =========
     */
    boolean manager_addCar(int carId, int numCars, int price) {
	return addReservation(carTable, carId, numCars, price);
    }

    /*
     * ==========================================================================
     * === manager_deleteCar -- Delete cars from a city -- Decreases available
     * car count (those not allocated to a customer) -- Fails if would make
     * available car count negative -- If decresed to 0, deletes entire entry --
     * Returns TRUE on success, else FALSE
     * ======================================
     * =======================================
     */
    boolean manager_deleteCar(int carId, int numCar) {
	/* -1 keeps old price */
	return addReservation(carTable, carId, -numCar, -1);
    }

    /*
     * ==========================================================================
     * === manager_addRoom -- Add rooms to a city -- Adding to an existing room
     * overwrite the price if 'price' >= 0 -- Returns TRUE on success, else
     * FALSE
     * ====================================================================
     * =========
     */
    boolean manager_addRoom(int roomId, int numRoom, int price) {
	return addReservation(roomTable, roomId, numRoom, price);
    }

    /*
     * ==========================================================================
     * === manager_deleteRoom -- Delete rooms from a city -- Decreases available
     * room count (those not allocated to a customer) -- Fails if would make
     * available room count negative -- If decresed to 0, deletes entire entry
     * -- Returns TRUE on success, else FALSE
     * ====================================
     * =========================================
     */
    boolean manager_deleteRoom(int roomId, int numRoom) {
	/* -1 keeps old price */
	return addReservation(roomTable, roomId, -numRoom, -1);
    }

    /*
     * ==========================================================================
     * === manager_addFlight -- Add seats to a flight -- Adding to an existing
     * flight overwrite the price if 'price' >= 0 -- Returns TRUE on success,
     * FALSE on failure
     * ==========================================================
     * ===================
     */
    boolean manager_addFlight(int flightId, int numSeat, int price) {
	return addReservation(flightTable, flightId, numSeat, price);
    }

    /*
     * ==========================================================================
     * === manager_deleteFlight -- Delete an entire flight -- Fails if customer
     * has reservation on this flight -- Returns TRUE on success, else FALSE
     * ====
     * =========================================================================
     */
    boolean manager_deleteFlight(int flightId) {
	Reservation reservation = flightTable.get(flightId);
	if (reservation == null) {
	    return false;
	}

	if (reservation.numUsed.get() > 0) {
	    return false; /* somebody has a reservation */
	}

	return addReservation(flightTable, flightId, -reservation.numTotal.get(), -1);
    }

    /*
     * ==========================================================================
     * === manager_addCustomer -- If customer already exists, returns failure --
     * Returns TRUE on success, else FALSE
     * ======================================
     * =======================================
     */
    boolean manager_addCustomer(int customerId) {
	Customer customer;

	if (customerTable.get(customerId) != null) {
	    return false;
	}

	customer = new Customer(customerId);
	Customer oldCustomer = customerTable.get(customerId);
	if (oldCustomer == null) {
	    customerTable.put(customerId, customer);
	} else {
	    TransactionSignaller.SIGNALLER.signalCommitFail();
	}

	return true;
    }

    /*
     * ==========================================================================
     * === manager_deleteCustomer -- Delete this customer and associated
     * reservations -- If customer does not exist, returns success -- Returns
     * TRUE on success, else FALSE
     * ==============================================
     * ===============================
     */
    boolean manager_deleteCustomer(int customerId) {
	Customer customer;
	TreeMap<Integer, Reservation> reservationTables[] = new TreeMap[Definitions.NUM_RESERVATION_TYPE];
	List_t<Reservation_Info> reservationInfoList;
	boolean status;

	customer = customerTable.get(customerId);
	if (customer == null) {
	    return false;
	}

	reservationTables[Definitions.RESERVATION_CAR] = carTable;
	reservationTables[Definitions.RESERVATION_ROOM] = roomTable;
	reservationTables[Definitions.RESERVATION_FLIGHT] = flightTable;

	/* Cancel this customer's reservations */
	reservationInfoList = customer.reservationInfoList;

	for (Reservation_Info reservationInfo : reservationInfoList) {
	    Reservation reservation = reservationTables[reservationInfo.type].get(reservationInfo.id);
	    if (reservation == null) {
		TransactionSignaller.SIGNALLER.signalCommitFail();
	    }
	    status = reservation.reservation_cancel();
	    if (!status) {
		TransactionSignaller.SIGNALLER.signalCommitFail();
	    }
	}

	status = customerTable.remove(customerId) != null ? true : false;
	if (!status) {
	    TransactionSignaller.SIGNALLER.signalCommitFail();
	}

	return true;
    }

    /*
     * ==========================================================================
     * === QUERY INTERFACE
     * ======================================================
     * =======================
     */

    /*
     * ==========================================================================
     * === queryNumFree -- Return numFree of a reservation, -1 if failure
     * ========
     * =====================================================================
     */
    int queryNumFree(TreeMap<Integer, Reservation> table, int id) {
	int numFree = -1;
	Reservation reservation = table.get(id);
	if (reservation != null) {
	    numFree = reservation.numFree.get();
	}

	return numFree;
    }

    /*
     * ==========================================================================
     * === queryPrice -- Return price of a reservation, -1 if failure
     * ============
     * =================================================================
     */
    int queryPrice(TreeMap<Integer, Reservation> table, int id) {
	int price = -1;
	Reservation reservation = table.get(id);
	if (reservation != null) {
	    price = reservation.price.get();
	}

	return price;
    }

    /*
     * ==========================================================================
     * === manager_queryCar -- Return the number of empty seats on a car --
     * Returns -1 if the car does not exist
     * ======================================
     * =======================================
     */
    int manager_queryCar(int carId) {
	return queryNumFree(carTable, carId);
    }

    /*
     * ==========================================================================
     * === manager_queryCarPrice -- Return the price of the car -- Returns -1 if
     * the car does not exist
     * ====================================================
     * =========================
     */
    int manager_queryCarPrice(int carId) {
	return queryPrice(carTable, carId);
    }

    /*
     * ==========================================================================
     * === manager_queryRoom -- Return the number of empty seats on a room --
     * Returns -1 if the room does not exist
     * ====================================
     * =========================================
     */
    int manager_queryRoom(int roomId) {
	return queryNumFree(roomTable, roomId);
    }

    /*
     * ==========================================================================
     * === manager_queryRoomPrice -- Return the price of the room -- Returns -1
     * if the room does not exist
     * ================================================
     * =============================
     */
    int manager_queryRoomPrice(int roomId) {
	return queryPrice(roomTable, roomId);
    }

    /*
     * ==========================================================================
     * === manager_queryFlight -- Return the number of empty seats on a flight
     * -- Returns -1 if the flight does not exist
     * ================================
     * =============================================
     */
    int manager_queryFlight(int flightId) {
	return queryNumFree(flightTable, flightId);
    }

    /*
     * ==========================================================================
     * === manager_queryFlightPrice -- Return the price of the flight -- Returns
     * -1 if the flight does not exist
     * ==========================================
     * ===================================
     */
    int manager_queryFlightPrice(int flightId) {
	return queryPrice(flightTable, flightId);
    }

    /*
     * ==========================================================================
     * === manager_queryCustomerBill -- Return the total price of all
     * reservations held for a customer -- Returns -1 if the customer does not
     * exist
     * ====================================================================
     * =========
     */
    int manager_queryCustomerBill(int customerId) {
	int bill = -1;
	Customer customer;

	customer = customerTable.get(customerId);

	if (customer != null) {
	    bill = customer.customer_getBill();
	}

	return bill;
    }

    /*
     * ==========================================================================
     * === RESERVATION INTERFACE
     * ================================================
     * =============================
     */

    /*
     * ==========================================================================
     * === reserve -- Customer is not allowed to reserve same (type, id)
     * multiple times -- Returns TRUE on success, else FALSE
     * ====================
     * =========================================================
     */
    static boolean reserve(TreeMap<Integer, Reservation> table, TreeMap<Integer, Customer> customerTable, int customerId, int id,
	    int type) {
	Customer customer;
	Reservation reservation;

	customer = customerTable.get(customerId);

	if (customer == null) {
	    return false;
	}

	reservation = table.get(id);
	if (reservation == null) {
	    return false;
	}

	if (!reservation.reservation_make()) {
	    return false;
	}

	if (!customer.customer_addReservationInfo(type, id, reservation.price.get())) {
	    /* Undo previous successful reservation */
	    boolean status = reservation.reservation_cancel();
	    if (!status) {
		TransactionSignaller.SIGNALLER.signalCommitFail();
	    }
	    return false;
	}

	return true;
    }

    /*
     * ==========================================================================
     * === manager_reserveCar -- Returns failure if the car or customer does not
     * exist -- Returns TRUE on success, else FALSE
     * ==============================
     * ===============================================
     */
    boolean manager_reserveCar(int customerId, int carId) {
	return reserve(carTable, customerTable, customerId, carId, Definitions.RESERVATION_CAR);
    }

    /*
     * ==========================================================================
     * === manager_reserveRoom -- Returns failure if the room or customer does
     * not exist -- Returns TRUE on success, else FALSE
     * ==========================
     * ===================================================
     */
    boolean manager_reserveRoom(int customerId, int roomId) {
	return reserve(roomTable, customerTable, customerId, roomId, Definitions.RESERVATION_ROOM);
    }

    /*
     * ==========================================================================
     * === manager_reserveFlight -- Returns failure if the flight or customer
     * does not exist -- Returns TRUE on success, else FALSE
     * ====================
     * =========================================================
     */
    boolean manager_reserveFlight(int customerId, int flightId) {
	return reserve(flightTable, customerTable, customerId, flightId, Definitions.RESERVATION_FLIGHT);
    }

    /*
     * ==========================================================================
     * === cancel -- Customer is not allowed to cancel multiple times -- Returns
     * TRUE on success, else FALSE
     * ==============================================
     * ===============================
     */
    static boolean cancel(TreeMap<Integer, Reservation> table, TreeMap<Integer, Customer> customerTable, int customerId, int id,
	    int type) {
	Customer customer;
	Reservation reservation;

	customer = customerTable.get(customerId);
	if (customer == null) {
	    return false;
	}

	reservation = table.get(id);
	if (reservation == null) {
	    return false;
	}

	if (!reservation.reservation_cancel()) {
	    return false;
	}

	if (!customer.customer_removeReservationInfo(type, id)) {
	    /* Undo previous successful cancellation */
	    boolean status = reservation.reservation_make();
	    if (!status) {
		TransactionSignaller.SIGNALLER.signalCommitFail();
	    }
	    return false;
	}

	return true;
    }

    /*
     * ==========================================================================
     * === manager_cancelCar -- Returns failure if the car, reservation, or
     * customer does not exist -- Returns TRUE on success, else FALSE
     * ============
     * =================================================================
     */
    boolean manager_cancelCar(int customerId, int carId) {
	return cancel(carTable, customerTable, customerId, carId, Definitions.RESERVATION_CAR);
    }

    /*
     * ==========================================================================
     * === manager_cancelRoom -- Returns failure if the room, reservation, or
     * customer does not exist -- Returns TRUE on success, else FALSE
     * ============
     * =================================================================
     */
    boolean manager_cancelRoom(int customerId, int roomId) {
	return cancel(roomTable, customerTable, customerId, roomId, Definitions.RESERVATION_ROOM);
    }

    /*
     * ==========================================================================
     * === manager_cancelFlight -- Returns failure if the flight, reservation,
     * or customer does not exist -- Returns TRUE on success, else FALSE
     * ========
     * =====================================================================
     */
    boolean manager_cancelFlight(int customerId, int flightId) {
	return cancel(flightTable, customerTable, customerId, flightId, Definitions.RESERVATION_FLIGHT);
    }
}

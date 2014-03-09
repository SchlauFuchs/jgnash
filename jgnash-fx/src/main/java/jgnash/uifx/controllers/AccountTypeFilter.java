/*
 * jGnash, a personal finance application
 * Copyright (C) 2001-2014 Craig Cavanaugh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jgnash.uifx.controllers;

/**
 * Account Type Filter interface
 * 
 * @author Craig Cavanaugh
 */
public interface AccountTypeFilter {

    public boolean isAccountVisible();

    public boolean isExpenseVisible();

    public boolean isHiddenVisible();

    public boolean isIncomeVisible();

    public void setAccountVisible(boolean visible);

    public void setExpenseVisible(boolean visible);

    public void setHiddenVisible(boolean visible);

    public void setIncomeVisible(boolean visible);
}

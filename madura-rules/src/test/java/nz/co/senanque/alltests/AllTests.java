/*******************************************************************************
 * Copyright (c)2014 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package nz.co.senanque.alltests;

import nz.co.senanque.base.ObjectTest;
import nz.co.senanque.decisiontable.PrototypeDecisionTableTest;
import nz.co.senanque.directed.DirectedRulesTest;
import nz.co.senanque.functions.FunctionTest;
import nz.co.senanque.listfunctions.HeapMonitorTest;
import nz.co.senanque.listfunctions.ListFunctionTest;
import nz.co.senanque.notknown.NotKnownRulesTest;
import nz.co.senanque.oneinvoice.OneInvoice2Test;
import nz.co.senanque.oneinvoice.OneInvoiceTest;
import nz.co.senanque.pizzaorder.PizzaOrderTest;
import nz.co.senanque.rulesparser.ParsePackageTest;
import nz.co.senanque.tableconstraint.TableConstraintTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
//	XJRTest.class,
	ObjectTest.class,
	FunctionTest.class,
	ListFunctionTest.class,
	HeapMonitorTest.class,
	OneInvoiceTest.class,
	OneInvoice2Test.class,
	PizzaOrderTest.class,
	PrototypeDecisionTableTest.class,
	DirectedRulesTest.class,
	NotKnownRulesTest.class,
	ParsePackageTest.class,
	TableConstraintTest.class
	})
public class AllTests
{
}

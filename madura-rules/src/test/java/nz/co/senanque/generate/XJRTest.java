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
package nz.co.senanque.generate;

import nz.co.senanque.generate.XJR;

import org.junit.Test;

public class XJRTest
{
	String TARGET_DIR = "generated-sources/xjc/";
	SampleExternalFunctions ss;

    @Test
    public void testGenerateBase()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/base/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/base/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.base");
        xjr.setXSDPackageName("nz.co.senanque.base");
        xjr.setBufferSize(3000);
        xjr.createClassReference().setName("nz.co.senanque.generate.SampleExternalFunctions");
        xjr.execute();
    }

    @Test
    public void testGenerateFunctions()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/functions/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/functions/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.functions");
        xjr.setXSDPackageName("nz.co.senanque.functions");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGenerateListFunctions()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/listfunctions/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/listfunctions/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.listfunctions");
        xjr.setXSDPackageName("nz.co.senanque.listfunctions");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGenerateOneInvoice()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/oneinvoice/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/oneinvoice/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.oneinvoice");
        xjr.setXSDPackageName("nz.co.senanque.oneinvoice");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGeneratePerformance()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/performance/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/performance/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.performance");
        xjr.setXSDPackageName("nz.co.senanque.performance");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGeneratePizzaOrder()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/pizzaorder/PizzaOrderRules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/pizzaorder/PizzaOrder.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.pizzaorder");
        xjr.setXSDPackageName("nz.co.senanque.pizzaorder");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGenerateDirected()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/directed/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/directed/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.directed");
        xjr.setXSDPackageName("nz.co.senanque.directed");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

    @Test
    public void testGenerateNotKnown()
    {
        XJR xjr = new XJR();
        xjr.setRules("src/test/resources/nz/co/senanque/notknown/rules.txt");
        xjr.setSchema("src/test/resources/nz/co/senanque/notknown/schema.xsd");
        xjr.setDestdir(TARGET_DIR);
        xjr.setPackageName("nz.co.senanque.notknown");
        xjr.setXSDPackageName("nz.co.senanque.notknown");
        xjr.setBufferSize(3000);
        xjr.execute();
    }

}

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

import nz.co.senanque.rules.annotations.Function;

/**
 * 
 * These are sample external functions that may be called directly from the rules
 * They must be static and they must have fixed arguments.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.2 $
 */
public class SampleExternalFunctions
{
    @Function
    public static String regex(String source, String pattern)
    {
        return "yes, that's okay";
    }
    @Function
    public static Double combine(Number a, Number b)
    {
        return a.doubleValue() + b.doubleValue();
    }

}

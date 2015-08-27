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
package nz.co.senanque.base;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Date;

/**
 * 
 * This is not actually a test, just a trial to ensue I understood how MessageFormat works.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.1 $
 */
public class MessageFormatTest
{

    /**
     * @param args
     */
    public static void main(String[] args)
    {
        int planet = 7;
        String event = "a disturbance in the Force";
        String result = MessageFormat.format(
                "At {1,time} on {1,date}, there was {2} on planet {0,number,integer}.",
                planet, new Date(), event);
        System.out.println(result);
        
        Class clazz = BusinessCustomer.class;
        try
        {
            Class superClass = clazz.getSuperclass();
            Field f = superClass.getDeclaredField("address");
        }
        catch (SecurityException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchFieldException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        


    }

}

/*******************************************************************************
 * Copyright 2011 Danny Kunz
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package org.omnaest.utils.propertyfile.content;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Abstract map reducing the {@link Map} interface to needed operations.
 * 
 * @author Omnaest
 * @param <K>
 * @param <V>
 */
public abstract class MapAbstract<K, V> implements Map<K, V>
{
  
  @Override
  public boolean isEmpty()
  {
    return this.size() == 0;
  }
  
  @Override
  public boolean containsKey( Object key )
  {
    return this.get( key ) != null;
  }
  
  @Override
  public boolean containsValue( Object value )
  {
    //
    boolean retval = false;
    
    //
    if ( value != null )
    {
      for ( V valueCurrent : this.values() )
      {
        //
        if ( value.equals( valueCurrent ) )
        {
          retval = true;
          break;
        }
        
      }
    }
    
    //
    return retval;
  }
  
  @Override
  public void putAll( Map<? extends K, ? extends V> map )
  {
    //
    if ( map != null )
    {
      for ( K key : map.keySet() )
      {
        //
        V value = map.get( key );
        
        //
        this.put( key, value );
      }
    }
  }
  
  @Override
  public Set<java.util.Map.Entry<K, V>> entrySet()
  {
    //
    Set<Entry<K, V>> retset = new HashSet<Map.Entry<K, V>>();
    
    //
    for ( final K key : this.keySet() )
    {
      //
      final V value = this.get( key );
      
      //
      Entry<K, V> entry = new Entry<K, V>()
      {
        
        @Override
        public K getKey()
        {
          return key;
        }
        
        @Override
        public V getValue()
        {
          return value;
        }
        
        @Override
        public V setValue( V valueNew )
        {
          //
          MapAbstract.this.put( key, valueNew );
          
          //
          return value;
        }
      };
      
      //
      retset.add( entry );
      
    }
    
    //
    return retset;
  }
  
}

/*
 * Copyright 2012 LinkedIn, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.linkedin.parseq.trace;

import com.linkedin.parseq.internal.trace.RelationshipBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.fail;

/**
 * @author Chi Chan (ckchan@linkedin.com)
 */
public class TestRelationshipBuilder
{
  RelationshipBuilder<Integer> _relationshipBuilder;
  @BeforeMethod
  public void setup()
  {
    _relationshipBuilder = new RelationshipBuilder<Integer>();
  }

  @AfterMethod
  public void tearDown()
  {
    _relationshipBuilder = null;
  }


  @Test
  public void TestSingleRelationship()
  {
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    assertEquals(1, _relationshipBuilder.getRelationships().size());
    assertEquals(0, _relationshipBuilder.getRelationships().iterator().next().getRelated().intValue());
    assertEquals(Relationship.POTENTIAL_PARENT_OF.name(), _relationshipBuilder.getRelationships().iterator().next().getRelationship());
  }

  @Test
  public void TestRemoveRelationship()
  {
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    _relationshipBuilder.removeRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    assertEquals(0, _relationshipBuilder.getRelationships().size());
  }

  @Test
  public void TestRemoveErrorRelationship()
  {
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    _relationshipBuilder.removeRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    try
    {
      _relationshipBuilder.removeRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
      fail("expected error");
    }
    catch(IllegalArgumentException ex)
    {
      //expected
    }

  }

  @Test
  public void TestAddDupRelationship()
  {
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    assertEquals(1, _relationshipBuilder.getRelationships().size());
    assertEquals(0, _relationshipBuilder.getRelationships().iterator().next().getRelated().intValue());
    assertEquals(Relationship.POTENTIAL_PARENT_OF.name(), _relationshipBuilder.getRelationships().iterator().next().getRelationship());

  }

  @Test
  public void TestAddMultiRelationship()
  {
    _relationshipBuilder.addRelationship(Relationship.POTENTIAL_PARENT_OF, 0);
    _relationshipBuilder.addRelationship(Relationship.CHILD_OF, 1);
    assertEquals(2, _relationshipBuilder.getRelationships().size());


    Iterator<Related<Integer>> it = _relationshipBuilder.getRelationships().iterator();
    Related<Integer> r1 = it.next();
    Related<Integer> r2 = it.next();
    if (r2.getRelationship().equals(Relationship.POTENTIAL_PARENT_OF.name()))
    {
      Related<Integer> temp = r2;
      r2 = r1;
      r1 = temp;
    }
    assertEquals(Relationship.POTENTIAL_PARENT_OF.name(), r1.getRelationship());
    assertEquals(0, r1.getRelated().intValue());
    assertEquals(Relationship.CHILD_OF.name(), r2.getRelationship());
    assertEquals(1, r2.getRelated().intValue());

  }
}

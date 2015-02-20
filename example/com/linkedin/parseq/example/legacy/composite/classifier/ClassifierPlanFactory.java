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

package com.linkedin.parseq.example.legacy.composite.classifier;

import com.linkedin.parseq.BaseTask;
import com.linkedin.parseq.Context;
import com.linkedin.parseq.Task;
import com.linkedin.parseq.example.legacy.composite.classifier.client.Client;
import com.linkedin.parseq.example.legacy.composite.classifier.client.Request;
import com.linkedin.parseq.example.legacy.composite.classifier.client.impl.GetNetworkRequest;
import com.linkedin.parseq.example.legacy.composite.classifier.client.impl.TruthMapRequest;
import com.linkedin.parseq.promise.Promise;
import com.linkedin.parseq.promise.Promises;
import com.linkedin.parseq.promise.SettablePromise;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.linkedin.parseq.Tasks.action;
import static com.linkedin.parseq.Tasks.par;
import static com.linkedin.parseq.Tasks.seq;

/**
 * @author Chris Pettitt (cpettitt@linkedin.com)
 */
public class ClassifierPlanFactory
{
  private final Client _client;

  public ClassifierPlanFactory(final Client client)
  {
    _client = client;
  }

  public Task<Map<Long, Classification>> classify(final long viewerId,
                                                  final Set<Long> vieweeIds)
  {
    return new ClassifierPlan(viewerId, vieweeIds);
  }

  private class ClassifierPlan extends BaseTask<Map<Long, Classification>>
  {
    private final long _viewerId;
    private final Set<Long> _unclassified;
    private final Map<Long, Classification> _classified = new HashMap<Long, Classification>();
    private final SettablePromise<Map<Long, Classification>> _result = Promises.settable();

    private ClassifierPlan(final long viewerId,
                           final Set<Long> unclassified)
    {
      super("ClassifierPlan[viewerId=" + viewerId + "]");
      _viewerId = viewerId;
      _unclassified = new HashSet<Long>(unclassified);
    }

    @Override
    public Promise<Map<Long, Classification>> run(final Context ctx)
    {
      // Network data is shared across classifiers, so we create it here
      final Task<Network> network = clientRequestTask(new GetNetworkRequest(_viewerId));

      // CLASSIFIERS

      // Self classification
      // Treat self as a special case. If we can classify all requested profiles
      // as self, then there is no need to make a remote call.
      final Task<?> selfClassifier = classifyTask(new SelfClassifier(_viewerId));

      // Full visibility classification
      final Task<?> directlyConnectedClassifier = connectedClassifyTask(network);
      final Task<?> invitedToGroupClassifier = truthMapQueryClassifyTask("GroupInvited", 1, Classification.FULL_VISIBILITY);
      final Task<?> messagedClassifier = truthMapQueryClassifyTask("Messaged", 2, Classification.FULL_VISIBILITY);

      // Partial visibility classification
      final Task<?> inNetworkClassifier = networkClassifyTask(network);
      final Task<?> sharesGroupClassifier = truthMapQueryClassifyTask("CommonGroups", 4, Classification.PARTIAL_VISIBILITY);

      // Default visibility (i.e. no visibility)
      final Task<?> defaultClassifier = classifyTask(DefaultClassifier.instance());

      // If we time out then we run local filters and return
      ctx.createTimer(1, TimeUnit.SECONDS, defaultClassifier);

      // ORDERING
      final Task<?> ordering =
          seq(selfClassifier,
              par(seq(network, directlyConnectedClassifier),
                  invitedToGroupClassifier,
                  messagedClassifier),
              par(inNetworkClassifier,
                  sharesGroupClassifier),
              defaultClassifier);
      ctx.run(ordering);

      return _result;
    }

    private Task<?> classifyTask(final Classifier classifier)
    {
      return action(classifier.getClass().getSimpleName(), new Runnable()
      {
        @Override
        public void run()
        {
          doClassify(classifier);
        }
      });
    }

    private Task<?> truthMapQueryClassifyTask(final String name,
                                              final int remainder,
                                              final Classification classification)
    {
      final Task<Map<Long, Boolean>> svcCall =
          clientRequestTask(new TruthMapRequest("get" + name, remainder, _unclassified));

      final Task<?> classifyResult = truthMapClassifyTask(name, classification, svcCall);

      return seq(svcCall, classifyResult);
    }

    private Task<?> truthMapClassifyTask(final String name,
                                         final Classification classification,
                                         final Promise<Map<Long, Boolean>> result)
    {
      return action(name + "Classifier", new Runnable()
      {
        @Override
        public void run()
        {
          doClassify(new TruthMapClassifier(classification, result.get()));
        }
      });
    }

    private <T> Task<T> clientRequestTask(final Request<T> request)
    {
      return new BaseTask<T>(request.getName())
      {
        @Override
        protected Promise<? extends T> run(final Context context) throws Exception
        {
          return _client.sendRequest(request);
        }
      };
    }

    private Task<?> connectedClassifyTask(final Task<Network> network)
    {
      return action("ConnectedClassifier", new Runnable()
      {
        @Override
        public void run()
        {
          doClassify(new ConnectedClassifier(network.get()));
        }
      });
    }

    private Task<?> networkClassifyTask(final Task<Network> network)
    {
      return action("NetworkClassifier", new Runnable()
      {
        @Override
        public void run()
        {
          doClassify(new NetworkClassifier(network.get()));
        }
      });
    }

    private void doClassify(final Classifier classifier)
    {
      for (Iterator<Long> it = _unclassified.iterator(); it.hasNext(); )
      {
        final long vieweeId = it.next();
        final Classification classification = classifier.classify(vieweeId);
        if (classification != null)
        {
          it.remove();
          _classified.put(vieweeId, classification);
        }
      }

      if (_unclassified.isEmpty())
      {
        _result.done(_classified);
      }
    }
  }
}

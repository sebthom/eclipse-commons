/*
 * Copyright 2021 by Sebastian Thomschke and contributors
 * SPDX-License-Identifier: EPL-2.0
 */
package de.sebthom.eclipse.commons.text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;

import net.sf.jstuff.core.validation.Args;

/**
 * @author Sebastian Thomschke
 */
public class Markers {

   private final String markerId;

   private Annotation[] activeMarkers = new Annotation[0];
   private IAnnotationModel activeMarkersAnnoModel;

   public Markers(@NonNull final String markerId) {
      Args.notNull("markerId", markerId);
      this.markerId = markerId;
   }

   private boolean removeMarkerAt(final int offset, final int length) {
      final var activeMarkers = this.activeMarkers;
      final var activeMarkersAnnoModel = this.activeMarkersAnnoModel;

      if (activeMarkersAnnoModel == null || activeMarkers.length == 0)
         return false;

      for (final var activeMarker : activeMarkers) {
         final var markerPos = activeMarkersAnnoModel.getPosition(activeMarker);
         if (markerPos == null) {
            continue;
         }
         if (offset == markerPos.getOffset() //
            && length == markerPos.getLength()) {
            activeMarkersAnnoModel.removeAnnotation(activeMarker);
            return true;
         }
      }
      return false;
   }

   public boolean removeMarkerAt(@NonNull final ITextSelection pos) {
      Args.notNull("pos", pos);
      return removeMarkerAt(pos.getOffset(), pos.getLength());
   }

   public boolean removeMarkerAt(@NonNull final Position pos) {
      Args.notNull("pos", pos);
      return removeMarkerAt(pos.getOffset(), pos.getLength());
   }

   public void removeMarkers() {
      if (activeMarkersAnnoModel != null && activeMarkers.length > 0) {
         if (activeMarkersAnnoModel instanceof IAnnotationModelExtension) {
            final var annoModelEx = (IAnnotationModelExtension) activeMarkersAnnoModel;
            annoModelEx.replaceAnnotations(activeMarkers, new HashMap<>());
         } else {
            for (final Annotation annotation : activeMarkers) {
               activeMarkersAnnoModel.removeAnnotation(annotation);
            }
         }
      }
   }

   public void setMarkers(@NonNull final IAnnotationModel annoModel, final List<Position> matches, final IProgressMonitor monitor) {
      Args.notNull("annoModel", annoModel);

      if (activeMarkersAnnoModel != null) {
         removeMarkers();
      }
      activeMarkersAnnoModel = annoModel;

      if (matches == null || matches.isEmpty())
         return;

      final var job = new Job("Setting markers") {
         @Override
         public IStatus run(final IProgressMonitor monitor) {
            final Map<Annotation, Position> newMarkers = new HashMap<>(matches.size());
            for (final var matchPos : matches) {
               newMarkers.put(new Annotation(markerId, false, null), matchPos);
            }

            if (annoModel instanceof IAnnotationModelExtension) {
               ((IAnnotationModelExtension) annoModel).replaceAnnotations(activeMarkers, newMarkers);
            } else {
               removeMarkers();
               for (final Map.Entry<Annotation, Position> entry : newMarkers.entrySet()) {
                  annoModel.addAnnotation(entry.getKey(), entry.getValue());
               }
            }
            activeMarkers = newMarkers.keySet().toArray(new Annotation[newMarkers.size()]);
            return Status.OK_STATUS;
         }
      };
      job.setPriority(Job.INTERACTIVE);
      job.run(monitor == null ? new NullProgressMonitor() : monitor);
   }
}

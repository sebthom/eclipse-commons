/*
 * Copyright 2021-2022 by Sebastian Thomschke and contributors
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
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;

/**
 * @author Sebastian Thomschke
 */
public class Markers {

   private final String markerId;
   private Annotation[] activeMarkers = new Annotation[0];
   private @Nullable IAnnotationModel activeMarkersAnnoModel;

   public Markers(final String markerId) {
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

   public boolean removeMarkerAt(final ITextSelection pos) {
      return removeMarkerAt(pos.getOffset(), pos.getLength());
   }

   public boolean removeMarkerAt(final Position pos) {
      return removeMarkerAt(pos.getOffset(), pos.getLength());
   }

   public void removeMarkers() {
      final var activeMarkersAnnoModel = this.activeMarkersAnnoModel;
      if (activeMarkersAnnoModel != null && activeMarkers.length > 0) {
         if (activeMarkersAnnoModel instanceof final IAnnotationModelExtension annoModelEx) {
            annoModelEx.replaceAnnotations(activeMarkers, new HashMap<>());
         } else {
            for (final Annotation annotation : activeMarkers) {
               activeMarkersAnnoModel.removeAnnotation(annotation);
            }
         }
      }
   }

   public void setMarkers(final IAnnotationModel annoModel, final @Nullable List<Position> matches) {
      setMarkers(annoModel, matches, new NullProgressMonitor());
   }

   public void setMarkers(final IAnnotationModel annoModel, final @Nullable List<Position> matches, final IProgressMonitor monitor) {
      if (activeMarkersAnnoModel != null) {
         removeMarkers();
      }
      activeMarkersAnnoModel = annoModel;

      if (matches == null || matches.isEmpty())
         return;

      final var job = new Job("Setting markers") {
         @Override
         public IStatus run(final @Nullable IProgressMonitor monitor) {
            final Map<Annotation, Position> newMarkers = new HashMap<>(matches.size());
            for (final var matchPos : matches) {
               newMarkers.put(new Annotation(markerId, false, null), matchPos);
            }

            if (annoModel instanceof final IAnnotationModelExtension annoModelExt) {
               annoModelExt.replaceAnnotations(activeMarkers, newMarkers);
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
      job.run(monitor);
   }
}

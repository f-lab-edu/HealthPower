package org.springframework.web.servlet;

import jakarta.servlet.ServletException;

public class ModelAndViewDefiningException extends ServletException {
        private final ModelAndView modelAndView;

        public ModelAndViewDefiningException(ModelAndView modelAndView) {
            this.modelAndView = modelAndView;
        }

        public ModelAndView getModelAndView() {
            return this.modelAndView;
        }
    }